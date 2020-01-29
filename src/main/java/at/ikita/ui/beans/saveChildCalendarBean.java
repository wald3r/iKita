package at.ikita.ui.beans;

import at.ikita.model.Child;
import at.ikita.model.ChildCalendar;
import at.ikita.model.Person;
import at.ikita.services.ChildCalendarService;
import org.primefaces.extensions.event.TimeSelectEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by lucas on 14.05.17.
 */

@Component
@Scope("view")
public class saveChildCalendarBean implements Serializable {

    @Autowired
    private ChildCalendarService childCalendarService;



    public saveChildCalendarBean() {}



    public void saveLunch(ChildCalendar childCalendar, boolean isLunch)
    {
        Child child = childCalendar.getChild();
        Date date = childCalendar.getDate();
        ChildCalendar cCal = childCalendarService.loadByChildAndDate(child, date);

        if(cCal == null) {
            // save entry to database
            childCalendarService.saveEntry(childCalendar);
        }
        else {
            childCalendar.setLunch(isLunch);
            // delete old calendar entity and save the new one
            childCalendarService.deleteEntry(cCal);
            childCalendarService.saveEntry(childCalendar);
        }
    }

    public void saveBringTime(ChildCalendar childCalendar, Date bringTime)
    {
        Child child = childCalendar.getChild();
        Date date = childCalendar.getDate();
        ChildCalendar cCal = childCalendarService.loadByChildAndDate(child, date);

        if(cCal == null) {
            // save entry to database
            childCalendarService.saveEntry(childCalendar);
        }
        else {
            childCalendar.setBringTime(bringTime);
            // delete old calendar entity and save the new one
            childCalendarService.deleteEntry(cCal);
            childCalendarService.saveEntry(childCalendar);
        }
    }

    public void savePickupTime(ChildCalendar childCalendar, Date pickupTime)
    {
        Child child = childCalendar.getChild();
        Date date = childCalendar.getDate();
        ChildCalendar cCal = childCalendarService.loadByChildAndDate(child, date);

        if(cCal == null) {
            // save entry to database
            childCalendarService.saveEntry(childCalendar);
        }
        else {
            childCalendar.setPickupTime(pickupTime);
            // delete old calendar entity and save the new one
            childCalendarService.deleteEntry(cCal);
            childCalendarService.saveEntry(childCalendar);
        }
    }

    public void savePickupPerson(ChildCalendar childCalendar, Person pickupPerson)
    {
        Child child = childCalendar.getChild();
        Date date = childCalendar.getDate();
        ChildCalendar cCal = childCalendarService.loadByChildAndDate(child, date);

        if(cCal == null) {
            // save entry to database
            childCalendarService.saveEntry(childCalendar);
        }
        else {
            childCalendar.setPickupPerson(pickupPerson);
            // delete old calendar entity and save the new one
            childCalendarService.deleteEntry(cCal);
            childCalendarService.saveEntry(childCalendar);
        }
    }

    public void timeSelectListener(TimeSelectEvent timeSelectEvent) {
        timeSelectEvent.getTime();
        /*
        FacesMessage msg =
                new FacesMessage(FacesMessage.SEVERITY_INFO, "Time select fired",
                        "Selected time: " + getFormattedTime(timeSelectEvent.getTime(), "HH:mm"));
        FacesContext.getCurrentInstance().addMessage(null, msg);
        */
    }

}
