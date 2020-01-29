package at.ikita.ui.beans;

import at.ikita.model.Child;
import at.ikita.model.ChildCalendar;
import at.ikita.model.Person;
import at.ikita.services.PersonService;
import at.ikita.ui.controllers.ChildCalendarController;
import at.ikita.ui.controllers.ConfigurationController2;
import at.ikita.ui.controllers.helper.DataWrapper;
import at.ikita.ui.controllers.helper.StringHolder;
import org.primefaces.event.ScheduleEntryMoveEvent;
import org.primefaces.event.ScheduleEntryResizeEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.DefaultScheduleEvent;
import org.primefaces.model.LazyScheduleModel;
import org.primefaces.model.ScheduleEvent;
import org.primefaces.model.ScheduleModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Calendar for parents to look up the presence, times and meals of their children
 * 
 * @author Lucas Markovic <lucas.markovic@student.uibk.ac.at>
 *
 */
@Component
@Scope("view")
public class ChildCalendarBean implements Serializable {

    private static final long serialVersionUID = 1L;
	
    @Autowired
    private ChildCalendarController childCalendarController;
    @Autowired
    private ConfigurationController2 configController;

    private ScheduleModel lazyEventModel;
    private ScheduleEvent event = new DefaultScheduleEvent();

    private Person user;
    private Child selectedChild;
    private String childID;


    /**
     * Method for init this class.
     */
    @PostConstruct
    public void init()
    {
        user = childCalendarController.getUser();
        selectedChild = childCalendarController.getSelectedChild();
    	
     	lazyEventModel = new LazyScheduleModel() {
        
     		@Override
        	public void loadEvents(Date start, Date end) {
        		List<ChildCalendar> childCalendars;
                DateFormat df = new SimpleDateFormat("HH:mm");
                Calendar iteratorCal = Calendar.getInstance();
                iteratorCal.setTime(start);
                iteratorCal.set(Calendar.HOUR_OF_DAY, 0);
                iteratorCal.set(Calendar.MINUTE, 0);
                iteratorCal.set(Calendar.SECOND, 0);

        		childCalendars = childCalendarController.getChildCalendarsPeriodWithNull(selectedChild, start, end);

        		// create events for scheduler out of childcalendars
                for (int i = 0; i < childCalendars.size(); i++)
                {
                    ChildCalendar cc = childCalendars.get(i);


                    if(cc != null) {
                        Date bringT = cc.getBringTime();
                        Date pickupT = cc.getPickupTime();

                        // build a string out of properties for schedule task
                        String bringTime = df.format(bringT);
                        String pickupTime = df.format(pickupT);
                        String lunch;
                        if(childCalendars.get(i).isLunch()) lunch = "ja";
                        else lunch = "nein";
                        String personStr;
                        if(cc.getPickupPerson() == null) personStr = "keine";
                        else personStr = cc.getPickupPerson().getFirstName() + " " + cc.getPickupPerson().getLastName();

                        String info = "Von: " + bringTime + "\nBis: " + pickupTime + "\nEssen: " + lunch + "\nPerson: " + personStr;

                        DataWrapper dataWrapper = wrapChildCalAndDaycareCal(iteratorCal.getTime(), cc);
                        //dataWrapper.addData(new Boolean(true), "Boolean", "present on that day");

                        DefaultScheduleEvent event0 = new DefaultScheduleEvent(info, bringT, pickupT, dataWrapper);
                        event0.setAllDay(true);
                        addEvent(event0);
                    }
                    else {
                        // set default bring and pickup times for disabled child calendars
                        iteratorCal.set(Calendar.HOUR_OF_DAY, selectedChild.getDefaultBringTime().getHours());
                        iteratorCal.set(Calendar.MINUTE, selectedChild.getDefaultBringTime().getMinutes());
                        Date bringT = iteratorCal.getTime();
                        iteratorCal.set(Calendar.HOUR_OF_DAY, selectedChild.getDefaultPickupTime().getHours());
                        iteratorCal.set(Calendar.MINUTE, selectedChild.getDefaultPickupTime().getMinutes());
                        Date pickupT = iteratorCal.getTime();
                        String info = "nicht anwesend";

                        // create dummy calendars for none existing days
                        ChildCalendar ccDummy = ChildCalendar.create(iteratorCal.getTime(), selectedChild);
                        ccDummy.setBringTime(bringT);
                        ccDummy.setPickupTime(pickupT);
                        ccDummy.setLunch(false);

                        DataWrapper dataWrapper = wrapChildCalAndDaycareCal(iteratorCal.getTime(), ccDummy);
                        //dataWrapper.addData(new Boolean(false), "Boolean", "not present on that day");

                        DefaultScheduleEvent event0 = new DefaultScheduleEvent(info, bringT, pickupT, dataWrapper);
                        event0.setAllDay(false);
                        addEvent(event0);
                    }
                    iteratorCal.add(Calendar.DAY_OF_MONTH, 1);
                }
        	}   
        };    
    }
    

    // helpers ============================================================================================================

    /**
     * converts the setted event und updates the database
     */
    public void updateChildCalendarEntry()
    {
        ChildCalendar cc = (ChildCalendar)((DataWrapper)event.getData()).getData(0);
        StringHolder pickupPersonIDStringHolder = (StringHolder)((DataWrapper)event.getData()).getData(5);
        String pickupPersonIDString = pickupPersonIDStringHolder.getValue();

        cc.setPickupPerson(childCalendarController.strIDToPerson(pickupPersonIDString));

        // allDay property is used for presence on that day
        if (event.isAllDay())
            childCalendarController.saveChildCalendar(cc);
        else
            childCalendarController.deleteChildCalendar(cc);
    }


    private DataWrapper wrapChildCalAndDaycareCal(Date date, ChildCalendar cc)
    {
        Integer minHourDaycare = configController.getMinHourDayCare(date);
        Integer maxHourDaycare = configController.getMaxHourDayCare(date);
        Integer minMinuteDaycare = configController.getMinMinuteDayCare(date);
        Integer maxMinuteDaycare = configController.getMaxMinuteDayCare(date);

        String personIDString = "-1";
        if(cc.getPickupPerson() != null)
            personIDString = cc.getPickupPerson().getId().toString();
        StringHolder str = new StringHolder();
        str.setValue(personIDString);

        Boolean isPast = childCalendarController.isPast(date);

        DataWrapper dw = new DataWrapper();
        dw.addData(cc, "ChildCalendar");
        dw.addData(minHourDaycare, "Integer", "minHourDaycare");
        dw.addData(maxHourDaycare, "Integer", "maxHourDaycare");
        dw.addData(minMinuteDaycare, "Integer", "minMinuteDaycare");
        dw.addData(maxMinuteDaycare, "Integer", "maxMinuteDaycare");
        dw.addData(str, "StringHolder", "pickupPersonIDString");
        dw.addData(isPast, "Boolean", "isTheGivenDateInThePast");

        return dw;
    }


    // events ===============================================================================================================
     
    public ScheduleEvent getEvent() {
        return event;
    }
 
    public void setEvent(ScheduleEvent event) {
        this.event = event;
    }
     
    public void addEvent(ActionEvent actionEvent) {
        if(event.getId() == null)
            lazyEventModel.addEvent(event);
        else
            lazyEventModel.updateEvent(event);
         
        event = new DefaultScheduleEvent();
    }
     
    public void onEventSelect(SelectEvent selectEvent) {
        event = (ScheduleEvent) selectEvent.getObject();
    }
     
    public void onDateSelect(SelectEvent selectEvent) {
        event = new DefaultScheduleEvent("", (Date) selectEvent.getObject(), (Date) selectEvent.getObject());
    }
     
    public void onEventMove(ScheduleEntryMoveEvent event) {
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Event moved", "Day delta:" + event.getDayDelta() + ", Minute delta:" + event.getMinuteDelta());
         
        addMessage(message);
    }
     
    public void onEventResize(ScheduleEntryResizeEvent event) {
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Event resized", "Day delta:" + event.getDayDelta() + ", Minute delta:" + event.getMinuteDelta());
         
        addMessage(message);
    }
     
    private void addMessage(FacesMessage message) {
        FacesContext.getCurrentInstance().addMessage(null, message);
    }


    // getter setter ===========================================================================================================

    public ScheduleModel getLazyEventModel() {
	return lazyEventModel;
    }
    public void setLazyEventModel(ScheduleModel lazyEventModel) {
	this.lazyEventModel = lazyEventModel;
}

}