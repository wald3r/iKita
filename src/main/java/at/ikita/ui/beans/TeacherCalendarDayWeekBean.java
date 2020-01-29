package at.ikita.ui.beans;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.model.SelectItem;

import org.primefaces.event.SelectEvent;
import org.primefaces.model.DefaultScheduleEvent;
import org.primefaces.model.LazyScheduleModel;
import org.primefaces.model.ScheduleEvent;
import org.primefaces.model.ScheduleModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import at.ikita.model.ChildCalendar;
import at.ikita.model.DayCareCalendar;
import at.ikita.model.Person;
import at.ikita.model.TeacherCalendar;
import at.ikita.services.DayCareCalendarService;
import at.ikita.services.TeacherCalendarChildService;
import at.ikita.services.TeacherCalendarDateService;
import at.ikita.services.TeacherCalendarDayCareService;
import at.ikita.services.TeacherCalendarTeacherService;
import at.ikita.ui.controllers.TeacherCalendarController;

@Component
@Scope("view")
public class TeacherCalendarDayWeekBean {
    
    private ScheduleModel lazyEventModel;
    
    private ScheduleEvent event = new DefaultScheduleEvent();
    
    @Autowired
    private TeacherCalendarController teacherCalendarController;
    
    @Autowired
    private TeacherCalendarChildService teacherCalendarChildService;
    
    @Autowired
    private TeacherCalendarTeacherService teacherCalendarTeacherService;
    
    @Autowired
    private TeacherCalendarDateService teacherCalendarDateService;
    
    @Autowired
    private TeacherCalendarDayCareService teacherCalendarDayCareService;
    
    @Autowired
    private DayCareCalendarService dayCareCalendarService;
    
    private List<TeacherCalendar> presentTeacher;
    
    private Map<String, Person> teacherObjects; // All teacher
    
    private List<SelectItem> teacherItems; // Selectable items from selectOneListBox
    
    private String selectedTeacherEmail; // Return value from selectOneListbox
        
    private List<ChildCalendar> presentChildren;
    
    private DayCareCalendar dayCareCalendar;
    
    private boolean dayCareClosed;
    
    @PostConstruct
    public void init() {
        lazyEventModel = new LazyScheduleModel() {
            
            @Override
            public void loadEvents(Date start, Date end) {
                Date currentDay = start;
                Date bringTime = null;
                Date pickupTime = null;
                
                Calendar tmpCalendar;
                
                while(currentDay.before(end)) {
                    DayCareCalendar dayCareCalendar = dayCareCalendarService.loadByDate(currentDay);
                    if(dayCareCalendar == null || 
                            (dayCareCalendar.getClosingTime().equals(Time.valueOf("00:00:00")) && 
                             dayCareCalendar.getOpeningTime().equals(Time.valueOf("00:00:00")))) {
                             DefaultScheduleEvent event = new DefaultScheduleEvent("Geschlossen", currentDay, currentDay, true);
                             event.setDescription("closed");
                             event.setStyleClass("closed-event");
                             addEvent(event);
                         } else {
                             presentTeacher = teacherCalendarController.getPresentTeacherByDate(currentDay);
                             for(TeacherCalendar i : presentTeacher) {
                                 DefaultScheduleEvent event = new DefaultScheduleEvent(teacherCalendarTeacherService.teacherDataToString(i), 
                                         currentDay, 
                                         currentDay, 
                                         true);
                                 event.setData(i);
                                 event.setDescription("teacher");
                                 event.setStyleClass("teacher-event");
                                 addEvent(event);
                             }

                             presentChildren = teacherCalendarController.getPresentChildrenByDate(currentDay);
                             for(ChildCalendar i : presentChildren) {
                                 bringTime = teacherCalendarChildService.getActualBringPickupTime(i, 1);
                                 pickupTime = teacherCalendarChildService.getActualBringPickupTime(i, 2);
                                 DefaultScheduleEvent event = new DefaultScheduleEvent(teacherCalendarChildService.childDataToString(i), 
                                         teacherCalendarDateService.timeToDate(bringTime, currentDay), 
                                         teacherCalendarDateService.timeToDate(pickupTime, currentDay)); 
                                 event.setData(i);
                                 event.setDescription("child");
                                 addEvent(event);
                             }

                             DefaultScheduleEvent event = new DefaultScheduleEvent("Maximalbelegung: " + teacherCalendarDayCareService.getMaxAttendancePerDay(currentDay), 
                                     currentDay, 
                                     currentDay, 
                                     true);
                             event.setDescription("attendance");
                             event.setStyleClass("attendance-event");
                             addEvent(event);
                         }
                    tmpCalendar = teacherCalendarDateService.addDay(currentDay);
                    currentDay = tmpCalendar.getTime();
                }    
            }   
        };
    }
    
    public void onEventSelect(SelectEvent selectEvent) {
        event = (ScheduleEvent) selectEvent.getObject();
    }
     
    public void onDateSelect(SelectEvent selectEvent) {
        Date eventDate = (Date) selectEvent.getObject();
        event = new DefaultScheduleEvent("", eventDate, eventDate);
        dayCareCalendar = dayCareCalendarService.loadByDate(eventDate);
        
        /* If no dayCareCalendar entry exists on this date, create a new one */
        if(dayCareCalendar == null) {
            dayCareCalendar = teacherCalendarDayCareService.initDayCareCalendar(eventDate);
        }
        
        try {
            dayCareClosed = dayCareCalendar.isClosed();
        } catch(NullPointerException e) {
            dayCareClosed = true;
        }
        
        try {
            dayCareCalendar.getMaxAttendance();
        } catch (NullPointerException e) {
            dayCareCalendar.setMaxAttendance(0);
        }
        
        initSelectOneListbox(eventDate);   
    }
    
    /**
     * Initialize a selectOneListeBox with not present teacher on a
     * specific date
     * 
     * @param date for initialization
     */
    public void initSelectOneListbox(Date date) {
        teacherItems = new ArrayList<>(); // The items which are clickable in the listbox (email, name)
        presentTeacher = teacherCalendarController.getPresentTeacherByDate(date);
        teacherObjects = teacherCalendarTeacherService.removePresentTeacher(presentTeacher); // Remove present teacher, do make them not selectable    
        selectedTeacherEmail = null; // flush attribute to avoid a preselected item
        
        /* Add all selectable teachers to the selectOneListbox.
         * Teacher which already present today should not be added to the listbox */
        for(Map.Entry<String, Person> i : teacherObjects.entrySet()) {
            teacherItems.add(new SelectItem(i.getKey(), i.getValue().getFirstName() + " " + i.getValue().getLastName()));
        }
    }
    
    /**
     * Save the selected Teacher. A new TeacherCalendar instance 
     * will be created and the at the listbox selected teacher will 
     * be assigned to the teacherCalendar model.  
     * 
     */
    public void saveTeacherCalendarEntry() {
        if(selectedTeacherEmail == null || selectedTeacherEmail.equals("")) {return;}
        Person teacher = null;
        Date eventDate = event.getStartDate();
        TeacherCalendar newTeacherCalendar = null;
       
        teacher = teacherObjects.get(selectedTeacherEmail); // load selected teacher (by key) from the teacher map
        newTeacherCalendar = TeacherCalendar.create(eventDate, teacher);
        teacherCalendarController.doSaveTeacherCalendar(newTeacherCalendar);
    }
    
    /**
     * Save a newly created or manipulated dayCareCalendar entry
     */
    public void saveDayCareCalendar() {
        dayCareCalendarService.saveEntry(dayCareCalendar);
    }
    
    /**
     * Reset dayCareCalendar attribute to avoid old data
     */
    public void flushDayCareCalendar() {
        this.dayCareCalendar = null;
    }
       
    /**
     * This method is used as a value change listener for 
     * a boolean checkbox.  
     */
    public void setDayCareCalendarClosed() {
        this.dayCareCalendar = teacherCalendarDayCareService.setDayCareCalendarClosed(dayCareCalendar, dayCareClosed);
    }
     
    public ScheduleModel getLazyEventModel() {
        return lazyEventModel;
    }

    public void setLazyEventModel(ScheduleModel lazyEventModel) {
        this.lazyEventModel = lazyEventModel;
    }

    public ScheduleEvent getEvent() {
        return event;
    }

    public void setEvent(ScheduleEvent event) {
        this.event = event;
    }
    
    public List<SelectItem> getTeacherItems() {
        return teacherItems;
    }

    public void setTeacherItems(List<SelectItem> teacherItems) {
        this.teacherItems = teacherItems;
    }

    public String getSelectedTeacherEmail() {
        return selectedTeacherEmail;
    }

    public void setSelectedTeacherEmail(String selectedTeacherEmail) {
        this.selectedTeacherEmail = selectedTeacherEmail;
    }

    public DayCareCalendar getDayCareCalendar() {
        return dayCareCalendar;
    }

    public void setDayCareCalendar(DayCareCalendar dayCareCalendar) {
        this.dayCareCalendar = dayCareCalendar;
    }

    public boolean isDayCareClosed() {
        return dayCareClosed;
    }

    public void setDayCareClosed(boolean dayCareClosed) {
        this.dayCareClosed = dayCareClosed;
    }

    public List<TeacherCalendar> getPresentTeacher() {
        return presentTeacher;
    }

    public void setPresentTeacher(List<TeacherCalendar> presentTeacher) {
        this.presentTeacher = presentTeacher;
    }

    public List<ChildCalendar> getPresentChildren() {
        return presentChildren;
    }

    public void setPresentChildren(List<ChildCalendar> presentChildren) {
        this.presentChildren = presentChildren;
    }
}
