package at.ikita.services;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.Calendar;
import java.util.Date;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import at.ikita.model.DayOfWeek;

/**
 * Service for date manipulations and conversions
 * 
 * @author Andre.Potocnik@student.uibk.ac.at
 *
 */
@Component
@Scope("request")
public class TeacherCalendarDateService {
    
    /**
     * Add one day (24h) to a DATE
     * 
     * @param date to manipulate
     * 
     * @return manipulated date
     */
    public Calendar addDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DATE, 1);
        
        return calendar;
    }
    
    /**
     * Combine present day and bring/pickup time of child
     * to a appropriate format for the scheduler
     * 
     * @param time bring/pickup time
     * @param today current day
     *
     * @return complete date (DD-MM-YY HH:MM:SS)
     */
    public Date timeToDate(Date time, Date today) {
        Calendar calendarTime = Calendar.getInstance();
        Calendar calendarDateTime = Calendar.getInstance();
        Date date;
        
        calendarTime.setTime(time);
        calendarDateTime.setTime(today);
        calendarDateTime.set(Calendar.HOUR_OF_DAY, calendarTime.get(Calendar.HOUR_OF_DAY));
        calendarDateTime.set(Calendar.MINUTE, calendarTime.get(Calendar.MINUTE));
        calendarDateTime.set(Calendar.SECOND, calendarTime.get(Calendar.SECOND));
        date = calendarDateTime.getTime();
                
        return date;
    }
}
