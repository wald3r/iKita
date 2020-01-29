package at.ikita.ui.controllers;

import at.ikita.configs.AppConfiguration2;
import at.ikita.model.*;
import at.ikita.services.ChildCalendarService;
import at.ikita.services.DayCareCalendarService;
import at.ikita.services.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.*;
import java.util.Calendar;

/**
 * Controller for Calendar per child daily view.
 *
 * Parents use this view to manage pickup times, lunch, etc. per child
 *
 * @author Lucas Markovic <lucas.markovic@student.uibk.ac.at>
 *
 *     TODO
 */


@Component
@Scope("view")
public class ChildCalendarController implements Serializable
{
    @Autowired
    private ChildCalendarService childCalendarService;
    @Autowired
    private PersonService personService;
    @Autowired
    private DayCareCalendarService dayCareCalendarService;

    private Person user;

    private List<Child> children;
    private Child selectedChild;
    private String childID;
    private List<SelectItem> childrenItems;

    private Person selectedGuardian;
    private String guardianID;
    private List<SelectItem> guardianItems;

    private List<ChildCalendar> selectedWeekChildCalendars;

    private Calendar currentCal;
    private Calendar selectedCal;

    AppConfiguration2 appConfig;


    // Constructor
    public ChildCalendarController() { }


    @PostConstruct
    public void init()
    {
        currentCal = Calendar.getInstance();

        // start with current week
        selectedCal = (Calendar)currentCal.clone();
        selectedCal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        selectedCal.set(Calendar.HOUR, 0);
        selectedCal.set(Calendar.MINUTE, 0);
        selectedCal.set(Calendar.SECOND, 0);

        // get current logged in user
        user = personService.getAuthenticatedUser();

        // create selectItems out of children set
        childrenItems = new ArrayList<SelectItem>();
        for(Child c : user.getChildren()) {
            String name = c.getFirstName() + " " + c.getLastName();
            String id = c.getId().toString();
            childrenItems.add(new SelectItem(id, name));
            if(selectedChild == null) selectedChild = c;
        }
        if(childrenItems.size() < 1) childrenItems.add(new SelectItem(-1, "none"));

        // create selectItems out of guardian set
        guardianItems = new ArrayList<SelectItem>();
        for(Person p : selectedChild.getGuardians()) {
            String name = p.getFirstName() + " " + p.getLastName();
            String id = p.getId().toString();
            guardianItems.add(new SelectItem(id, name));
            //if(selectedGuardian == null) selectedGuardian = p;
        }
        for(Person p : selectedChild.getParents()) {
            String name = p.getFirstName() + " " + p.getLastName();
            String id = p.getId().toString();
            guardianItems.add(new SelectItem(id, name));
            //if(selectedGuardian == null) selectedGuardian = p;
        }
        if(guardianItems.size() < 1) guardianItems.add(new SelectItem(-1, "none"));

        appConfig = new AppConfiguration2();
    }



// selectors =====================================================================================================


    public void setPreviousWeek()
    {
        // TODO Zeitraum eingrenzen???
        selectedCal.add(Calendar.WEEK_OF_YEAR, -1);
        selectedCal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        fetchSelectedWeekChildCalendars();
    }

    public void setNextWeek()
    {
        selectedCal.add(Calendar.WEEK_OF_YEAR, 1);
        selectedCal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        fetchSelectedWeekChildCalendars();
    }



// convert =====================================================================================================


    public List<ChildCalendar> getChildCalendarsPeriodWithNull(Child child, Date startDate, Date endDate)
    {
        // check if there's a child to this parent
        if(childID != null) {
            for(Child c : user.getChildren()) {
                if(c.getId() == Integer.parseInt(childID)) {
                    selectedChild = c;
                }
            }
        }
        if (selectedChild == null) { return null; }

        Calendar endCal = Calendar.getInstance();
        endCal.setTime(endDate);
        Calendar iteratorCal = Calendar.getInstance();
        iteratorCal.setTime(startDate);
        Calendar converterCal = Calendar.getInstance();
        List<ChildCalendar> ccList = new ArrayList<>();

        while(iteratorCal.compareTo(endCal) <= 0) {
            ChildCalendar cc = childCalendarService.loadByChildAndDate(child, iteratorCal.getTime());
            // if there's any calendar
            if (cc == null) {
                ccList.add(null);
            }
            else {
                if (cc.getBringTime() == null) {
                    Date bringT = child.getDefaultBringTime();
                    iteratorCal.set(Calendar.HOUR_OF_DAY, bringT.getHours());
                    iteratorCal.set(Calendar.MINUTE, bringT.getMinutes());
                    cc.setBringTime(iteratorCal.getTime());
                }
                if (cc.getPickupTime() == null) {
                    Date pickupT = child.getDefaultPickupTime();
                    iteratorCal.set(Calendar.HOUR_OF_DAY, pickupT.getHours());
                    iteratorCal.set(Calendar.MINUTE, pickupT.getMinutes());
                    cc.setPickupTime(iteratorCal.getTime());
                }

                // convert bring time
                converterCal.setTime(cc.getBringTime());
                converterCal.set(Calendar.YEAR, iteratorCal.get(Calendar.YEAR));
                converterCal.set(Calendar.MONTH, iteratorCal.get(Calendar.MONTH));
                converterCal.set(Calendar.DAY_OF_MONTH, iteratorCal.get(Calendar.DAY_OF_MONTH));
                cc.setBringTime(converterCal.getTime());
                // convert pickup time
                converterCal.setTime(cc.getPickupTime());
                converterCal.set(Calendar.YEAR, iteratorCal.get(Calendar.YEAR));
                converterCal.set(Calendar.MONTH, iteratorCal.get(Calendar.MONTH));
                converterCal.set(Calendar.DAY_OF_MONTH, iteratorCal.get(Calendar.DAY_OF_MONTH));
                cc.setPickupTime(converterCal.getTime());

                ccList.add(cc);
            }
            iteratorCal.add(Calendar.DAY_OF_YEAR, 1);
        }

        return ccList;
    }

    public List<ChildCalendar> getChildCalendarsPeriodFilled(Child child, Date startDate, Date endDate)
    {
        List<ChildCalendar> ccList = getChildCalendarsPeriodWithNull(child, startDate, endDate);

        Calendar iteratorCal = Calendar.getInstance();
        iteratorCal.setTime(startDate);
        for(int i=0; i<ccList.size(); i++)
        {
            // if there's any calendar
            if (ccList.get(i) == null) {
                ChildCalendar cc = ChildCalendar.create(iteratorCal.getTime(), child);
                Date bringT = child.getDefaultBringTime();
                iteratorCal.set(Calendar.HOUR_OF_DAY, bringT.getHours());
                iteratorCal.set(Calendar.MINUTE, bringT.getMinutes());
                cc.setBringTime(iteratorCal.getTime());
                Date pickupT = child.getDefaultPickupTime();
                iteratorCal.set(Calendar.HOUR_OF_DAY, pickupT.getHours());
                iteratorCal.set(Calendar.HOUR_OF_DAY, pickupT.getMinutes());
                cc.setPickupTime(iteratorCal.getTime());
                cc.setLunch(false);
                ccList.set(i, cc);
            }
            iteratorCal.add(Calendar.DAY_OF_YEAR, 1);
        }

        return ccList;
    }



    /**
     * This function is the core of this class.
     * It take the the selected child and the selected week and creates a list of childCalendar objects
     */
    public void fetchSelectedWeekChildCalendars()
    {
        selectedWeekChildCalendars = null;

        // check if there's a child to this parent
        if(childID != null) {
            for(Child c : user.getChildren()) {
                if(c.getId() == Integer.parseInt(childID)) {
                    selectedChild = c;
                }
            }
        }
        if (selectedChild == null) { return; }

        // set the date for the selected week to monday
        Calendar tempCal = (Calendar)selectedCal.clone();
        tempCal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        List<ChildCalendar> cCals = new ArrayList<>();
        List<Date> currentWeek = new ArrayList<>();

        // get the whole week Monday-Saturday
        for (int i = 0; i < 6; i++) {
            currentWeek.add(tempCal.getTime());
            tempCal.add(Calendar.DAY_OF_MONTH, 1);
        }
        // skip sunday
        tempCal.add(Calendar.DAY_OF_MONTH, 1);

        // get childCalendars from this week
        // if not existing create a new one
        for (int i = 0; i < 6; i++) {
            ChildCalendar cCal = childCalendarService.loadByChildAndDate(selectedChild, currentWeek.get(i));
            // if there's any calendar
            if (cCal == null) {
                cCal = ChildCalendar.create(currentWeek.get(i), selectedChild);
                cCal.setLunch(false);
            }
            if(cCal.getBringTime() == null) {
                tempCal.set(Calendar.HOUR, 8);
                cCal.setBringTime(tempCal.getTime());
            }
            if(cCal.getPickupTime() == null) {
                tempCal.set(Calendar.HOUR, 17);
                cCal.setPickupTime(tempCal.getTime());
            }
            cCals.add(cCal);
        }

        selectedWeekChildCalendars = cCals;
    }

    /**
     * use getSelectedChildCalendars'day']() to get a list of the specific day of selectedChildCalendars
     * @param weekDay is a placeholder for Calendar.'day'
     * @return a list of ChildCalendar on specific days
     */
    public List<ChildCalendar> getSelectedChildCalendarsDay(int weekDay)
    {
        if (selectedChild == null)
            return null;
        fetchSelectedWeekChildCalendars();

        List<ChildCalendar> cCals = new ArrayList<>();

        Calendar cal = Calendar.getInstance();
        for(ChildCalendar cCal : selectedWeekChildCalendars) {
            cal.setTime(cCal.getDate());
            if(cal.get(Calendar.DAY_OF_WEEK) == weekDay)
                cCals.add(cCal);
        }
        return cCals;
    }
    public List<ChildCalendar> getSelectedChildCalendarsMonday() { return getSelectedChildCalendarsDay(Calendar.MONDAY); }
    public List<ChildCalendar> getSelectedChildCalendarsTuesday() { return getSelectedChildCalendarsDay(Calendar.TUESDAY); }
    public List<ChildCalendar> getSelectedChildCalendarsWednesday() { return getSelectedChildCalendarsDay(Calendar.WEDNESDAY); }
    public List<ChildCalendar> getSelectedChildCalendarsThursday() { return getSelectedChildCalendarsDay(Calendar.THURSDAY); }
    public List<ChildCalendar> getSelectedChildCalendarsFriday() { return getSelectedChildCalendarsDay(Calendar.FRIDAY); }
    public List<ChildCalendar> getSelectedChildCalendarsSaturday() { return getSelectedChildCalendarsDay(Calendar.SATURDAY); }


    public void deleteChildCalendar(ChildCalendar childCalendar)
    {
        childCalendarService.deleteEntry(childCalendar);
    }

    /**
     * saves a ChildCalendar object if none other object on this date exists or else
     * deletes the old one and replaces with current object.
     * @param childCalendar object what will be saved in database
     */
    public void saveChildCalendar(ChildCalendar childCalendar)
    {
        // if bring or pickup time is default -> null
        if(compareHoursAndMinutes(childCalendar.getChild().getDefaultBringTime(), childCalendar.getBringTime()))
            childCalendar.setBringTime(null);
        if(compareHoursAndMinutes(childCalendar.getChild().getDefaultPickupTime(), childCalendar.getPickupTime()))
            childCalendar.setPickupTime(null);

        ChildCalendar cCal = childCalendarService.loadByChildAndDate(childCalendar.getChild(), childCalendar.getDate());
        if(cCal != null) {
            // delete old calendar entity and save the new one
            childCalendarService.deleteEntry(cCal);
        }
        // save entry to database
        childCalendarService.saveEntry(childCalendar);
    }

    public void saveWeekChildCalendars(List<ChildCalendar> childCalendars)
    {
        for(ChildCalendar c : childCalendars)
            saveChildCalendar(c);
    }

    public void saveThisWeekChildCalendars()
    {
        saveWeekChildCalendars(selectedWeekChildCalendars);
    }


    public String getMonth()
    {
        int month = selectedCal.get(Calendar.MONTH)+1;
        String monthString;
        switch (month) {
            case 1:  monthString = "January";
                break;
            case 2:  monthString = "February";
                break;
            case 3:  monthString = "March";
                break;
            case 4:  monthString = "April";
                break;
            case 5:  monthString = "May";
                break;
            case 6:  monthString = "June";
                break;
            case 7:  monthString = "July";
                break;
            case 8:  monthString = "August";
                break;
            case 9:  monthString = "September";
                break;
            case 10: monthString = "October";
                break;
            case 11: monthString = "November";
                break;
            case 12: monthString = "December";
                break;
            default: monthString = "Invalid month";
                break;
        }
        return monthString;
    }

    // returns current week as String
    public String getWeekString()
    {
        int week = selectedCal.get(Calendar.WEEK_OF_YEAR);
        return "Kalenderwoche: " + week;
    }


    // compares two dates to hours and minutes *not* year, month, day and Seconds
    public Boolean compareHoursAndMinutes(Date date1, Date date2)
    {
        if(date1.getHours() != date2.getHours())
            return false;
        if(date1.getMinutes() != date2.getMinutes())
            return false;
        return true;
    }

    // compares two dates to year, month and day *not* Hour, Minutes and Seconds
    public Boolean compareDateWithoutTime(Date date1, Date date2)
    {
        if(date1.getYear() != date2.getYear())
            return false;
        if(date1.getMonth() != date2.getMonth())
            return false;
        if(date1.getDate() != date2.getDate())
            return false;
        return true;
    }

    /**
     * related person to the child converts from ID to Person
     * @param id the Id from a Person as String
     * @return Person
     */
    public Person strIDToPerson(String id)
    {
        Person person = null;
        for(Person p : selectedChild.getGuardians()) {
            if(p.getId() == Integer.parseInt(id)) {
                person = p;
            }
        }
        for(Person p : selectedChild.getParents()) {
            if(p.getId() == Integer.parseInt(id)) {
                person = p;
            }
        }
        return person;
    }

    // checks is a date is in the past
    public Boolean isPast(Date date)
    {
        Calendar calNow = Calendar.getInstance();
        Calendar calDate = Calendar.getInstance();
        calDate.setTime(date);

        int result = calNow.compareTo(calDate);
        if(result < 0)
            return false;
        else
            return true;
    }



// getter setter for properties ===================================================================================
// some setters arent needed but must be implimented for spring...

    public List<SelectItem> getChildrenItems() { return childrenItems; }
    public void setChildrenItems(List<SelectItem> childrenItems)
    {
        this.childrenItems = childrenItems;
    }

    public List<ChildCalendar> getSelectedWeekChildCalendars()
    {
        return selectedWeekChildCalendars;
    }
    public void setSelectedWeekChildCalendars(List<ChildCalendar> cCals) { selectedWeekChildCalendars = cCals; }

    public String getChildID() { return childID; }
    public void setChildID(String id)
    {
        childID = id;
        for(Child c : user.getChildren()) {
            if(c.getId() == Integer.parseInt(id)) {
                selectedChild = c;
            }
        }
        fetchSelectedWeekChildCalendars();
    }

    public Child getSelectedChild() { return selectedChild; }
    public void setSelectedChild(Child child) { selectedChild = child; }

    public List<SelectItem> getGuardianItems() { return guardianItems; }
    public void setSelectedGuardian(List<SelectItem> guardianItems)
    {
        this.guardianItems = guardianItems;
    }

    public String getGuardianID() { return guardianID; }
    public void setGuardianID(String id)
    {
        guardianID = id;
        for(Person p : selectedChild.getGuardians()) {
            if(p.getId() == Integer.parseInt(id)) {
                selectedGuardian = p;
            }
        }
    }

    // for scheduler

    public List<Child> getChildren() { return children; }

    public Child getChild() { return selectedChild; }
    public void setChild(Child child) { selectedChild = child; }

    public Person getUser() { return user; }



}
