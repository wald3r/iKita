package at.ikita.services;

import java.sql.Time;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import at.ikita.model.DayCareCalendar;

/**
 * Service for the teacherCalendarDay/Week/Month to show and
 * manipulate dayCareCalendar entries.
 * 
 * 
 * @author Andre.Potocnik@student.uibk.ac.at
 *
 */
@Component
@Scope("request")
public class TeacherCalendarDayCareService {
    
    @Autowired
    private DayCareCalendarService dayCareCalendarService;
    
    /**
     * Get the maximal attendant value on a specific day.
     * Return String with max children i if maxAttendance is set,
     * "not set" sign if maxAttendance is null or no
     * dayCareCalendar entry exists for this day
     *  
     * @param date of the desired day
     * @return String number of maxAttendance or "not set" sign
     */
    public String getMaxAttendancePerDay(Date date) {
        DayCareCalendar dayCareCalendar = dayCareCalendarService.loadByDate(date);
        
        /* If no dayCareCalendar entry exists, return "not set" sign */
        if(dayCareCalendar == null) {
            return "-";
        }
        
        /* If getAttendance() throws NPE, maxAttendance is not set */
        try {
            Integer maxAttendance = new Integer(dayCareCalendar.getMaxAttendance());
            return maxAttendance.toString();
        } catch (NullPointerException e) {
            return "-";
        }
    }
    
    /**
     * Initialize a dayCareCalendar entry with default values
     * 
     * @param date of dayCareCalendar entry
     * 
     * @return initialized dayCareCalendar entry
     */
    public DayCareCalendar initDayCareCalendar(Date date) {
        DayCareCalendar dayCareCalendar = DayCareCalendar.create(date);
        dayCareCalendar.setMaxAttendance(0);
        dayCareCalendar.setMeal1Description("");
        dayCareCalendar.setMeal2Description("");
        dayCareCalendar.setMealPrice(0.0);
        dayCareCalendar.setClosed();
        
        return dayCareCalendar;
    }
    
    /**
     * Set opening/closing time on dayCareCalendar entry to
     * 00:00:00 to indicate that iKita is closed on this day.
     *      *
     * @param dayCareCalendar to manipulate
     * @param dayCareClosed flag if it is already in the closed state
     * 
     * @return the resulting dayCareCalendar
     */
    public DayCareCalendar setDayCareCalendarClosed(DayCareCalendar dayCareCalendar, boolean dayCareClosed) {
        /* 
         * Make sure that dayCareCalendar entry is not 00:00:00
         * after a sequence dayCareOpen->dayCareClosed->dayCareOpen
         * sequence.
         */
        if(dayCareClosed) {
            dayCareCalendar.setOpeningTime(Time.valueOf("00:00:00"));
            dayCareCalendar.setClosingTime(Time.valueOf("00:00:00"));
            dayCareCalendar.setMeal1Description("");
            dayCareCalendar.setMeal2Description("");
            dayCareCalendar.setMealPrice(0.0);
            dayCareCalendar.setMaxAttendance(0);
        } else {
            dayCareCalendar.setOpeningTime(null);
            dayCareCalendar.setClosingTime(null);
        }
        
        return dayCareCalendar;
    }
}
