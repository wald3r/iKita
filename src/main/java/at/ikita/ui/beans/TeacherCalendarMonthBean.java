package at.ikita.ui.beans;
 
import java.io.Serializable;
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

import at.ikita.model.DayCareCalendar;
import at.ikita.model.Person;
import at.ikita.model.TeacherCalendar;
import at.ikita.services.DayCareCalendarService;
import at.ikita.services.TeacherCalendarDateService;
import at.ikita.services.TeacherCalendarDayCareService;
import at.ikita.services.TeacherCalendarTeacherService;
import at.ikita.ui.controllers.TeacherCalendarController;

/**
 * Calendar for teachers which shows present children's,
 * attendant teachers, booked meals
 * 
 * @author Andre.Potocnik@student.uibk.ac.at
 *
 */
@Component
@Scope("view")
public class TeacherCalendarMonthBean implements Serializable {
	
    private static final long serialVersionUID = 1L;
    
    @Autowired
    private TeacherCalendarController teacherCalendarController;
    
    @Autowired
    private TeacherCalendarDateService teacherCalendarDateService;
    
    @Autowired
    private TeacherCalendarTeacherService teacherCalendarTeacherService;
    
    @Autowired
    private TeacherCalendarDayCareService teacherCalendarDayCareService;
    
    @Autowired
    private DayCareCalendarService dayCareCalendarService;
        
    private List<TeacherCalendar> presentTeacher;
    
    private Map<String, Person> teacherObjects; // All teacher
    
    private List<SelectItem> teacherItems; // Selectable items from selectOneListBox
    
    private String selectedTeacherEmail; // Return value from selectOneListbox
    		
    private ScheduleModel lazyEventModel;
	
    private ScheduleEvent event = new DefaultScheduleEvent();
    
    private DayCareCalendar dayCareCalendar;
    
    private boolean dayCareClosed;
	
    /**
     * Scheduler initialization, fill current scheduler view
     * with present children, present teachers and booked
     * meals
     */
    @PostConstruct
    public void init() {
    	
     	lazyEventModel = new LazyScheduleModel() {
        
     		@Override
        	public void loadEvents(Date start, Date end) {
        		List<String> presentChildrenByDayPeriod;
        		List<String> presentTeacherByDayPeriod;
        		List<String> bookedMealsByDayPeriod;
        		Calendar tmpCalendar;
        		Date currentDay = start;
        		
        		presentChildrenByDayPeriod = getAmountPresentChildrenByDay(start, end);
        		presentTeacherByDayPeriod = getAmountPresentTeacherByDay(start, end);
        		bookedMealsByDayPeriod = getAmountBookedMealsByDay(start, end);
        		
        		for(int i = 0; i < presentChildrenByDayPeriod.size(); i++) {
        		    /* Check if ikita is closed in if branch. Create closed event, ignore other events */
        		    DayCareCalendar dayCareCalendar = dayCareCalendarService.loadByDate(currentDay);
        		    if(dayCareCalendar == null || dayCareCalendar.isClosed()) {
        		        DefaultScheduleEvent event = new DefaultScheduleEvent("Geschlossen", currentDay, currentDay, true);
        		        event.setDescription("closed");
        		        event.setStyleClass("closed-event");
        		        addEvent(event);
        		    } else {        		    
            		    DefaultScheduleEvent event = new DefaultScheduleEvent("Paedagogen: " + presentTeacherByDayPeriod.get(i), currentDay, currentDay, true);
            		    event.setStyleClass("teacher-event");
                        event.setDescription("teacher");
            		    addEvent(event);
                        
                        event = new DefaultScheduleEvent("Kinder: " + presentChildrenByDayPeriod.get(i), currentDay, currentDay);
                        event.setDescription("child");
            			addEvent(event);
            			
            			event = new DefaultScheduleEvent("Essen: " + bookedMealsByDayPeriod.get(i), currentDay, currentDay);
            			event.setDescription("eat");
            			addEvent(event);
            			
            			event = new DefaultScheduleEvent("Maximalbelegung: " + teacherCalendarDayCareService.getMaxAttendancePerDay(currentDay), currentDay, currentDay);
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
    
    /**
     * Count present children on a day in the period between
     * start and end date. Start and end date are given by
     * the schedulers current view.
     * 
     * @param start begin of period to count children day by day
     * @param end end of period to count children day by day
     *
     * @return list of days and present children on day
     */
    public List<String> getAmountPresentChildrenByDay(Date start, Date end) {
    	List<String> presentChildrenByDayPeriod = new ArrayList<String>();
    	Integer presentChildrenByDay;
    	Date currentDay = start;
    	Calendar tmpCalendar;
    	    	
    	while(currentDay.before(end)) {
    		presentChildrenByDay = new Integer(teacherCalendarController.countPresentChildrenByDate(currentDay));
    		presentChildrenByDayPeriod.add(presentChildrenByDay.toString());
    		tmpCalendar = teacherCalendarDateService.addDay(currentDay);
    		currentDay = tmpCalendar.getTime();  		
    	}
    	
    	return presentChildrenByDayPeriod;
    }
    
    /**
     * Count present teacher on a day in the period between
     * start and end date. Start and end date are given by
     * the schedulers current view.
     * 
     * @param start begin of period to count children day by day
     * @param end end of period to count children day by day
     *
     * @return list of days and present teachers on day
     */
    public List<String> getAmountPresentTeacherByDay(Date start, Date end) {
    	List<String> presentTeacherByDayPeriod = new ArrayList<String>();
    	Integer presentTeacherByDay;
    	Date currentDay = start;
    	Calendar tmpCalendar;
    	
    	while(currentDay.before(end)) {
    		presentTeacherByDay = new Integer(teacherCalendarController.countPresentTeacherByDate(currentDay));
    		presentTeacherByDayPeriod.add(presentTeacherByDay.toString());
    		tmpCalendar = teacherCalendarDateService.addDay(currentDay);
    		currentDay = tmpCalendar.getTime();
    	}
    	
    	return presentTeacherByDayPeriod;
    }
    
    /**
     * Count present teacher on a day in the period between
     * start and end date. Start and end date are given by
     * the schedulers current view.
     *
     * @param start begin of period to count children day by day
     * @param end end of period to count children day by day
     
     * @return list of days and booked meals by teachers and childrens
     */
    public List<String> getAmountBookedMealsByDay(Date start, Date end) {
    	List<String> bookedMealsByPeriod = new ArrayList<String>();
    	Integer bookedMeals;
    	Date currentDay = start;
    	Calendar tmpCalendar;
    	
    	while(currentDay.before(end)) {
    		bookedMeals = new Integer(teacherCalendarController.countChildrenMealsByDate(currentDay));
    		bookedMeals += teacherCalendarController.countTeacherMealsByDate(currentDay);
    		bookedMealsByPeriod.add(bookedMeals.toString());
    		tmpCalendar = teacherCalendarDateService.addDay(currentDay);
    		currentDay = tmpCalendar.getTime();
    	}
    	
    	return bookedMealsByPeriod;
    }
    
    /**
     * This method is fired if a event is clicked at
     * the scheduler. It determines the event type
     * and handles appropriate.
     * 
     * @param selectEvent which have been clicked
     */
    public void onEventSelect(SelectEvent selectEvent) {
        event = (DefaultScheduleEvent) selectEvent.getObject();
        Date eventDate = event.getStartDate();
        String eventDescription = event.getDescription(); 
        
        if(eventDescription.equals("teacher")) {
            initSelectOneListbox(eventDate);
        } else {
            dayCareCalendar = dayCareCalendarService.loadByDate(eventDate);
            if(dayCareCalendar == null) {
                dayCareCalendar = teacherCalendarDayCareService.initDayCareCalendar(eventDate);
            }
            dayCareClosed = dayCareCalendar.isClosed();
            
            try {
                dayCareCalendar.getMaxAttendance();
            } catch (NullPointerException e) {
                dayCareCalendar.setMaxAttendance(0);
            }
        }
    }
    
    /**
     * This method is fired if a date is clicked at
     * the scheduler. It initializes all the 
     * necessary stuff for event manipulation
     * 
     * @param selectEvent only carries the date of the event
     */
    public void onDateSelect(SelectEvent selectEvent) {
        Date eventDate = (Date) selectEvent.getObject();
        event = new DefaultScheduleEvent("", eventDate, eventDate);
        dayCareCalendar = dayCareCalendarService.loadByDate(eventDate);
        
        
        /* If no dayCareCalendar entry exists on this date, create a new one w. */
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
        selectedTeacherEmail = null;
        
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
    
    public List<TeacherCalendar> getPresentTeacher() {
        return presentTeacher;
    }

    public void setPresentTeacher(List<TeacherCalendar> presentTeacher) {
        this.presentTeacher = presentTeacher;
    }

    public Map<String, Person> getTeacherObjects() {
        return teacherObjects;
    }

    public void setTeacherObjects(Map<String, Person> teacherObjects) {
        this.teacherObjects = teacherObjects;
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
}