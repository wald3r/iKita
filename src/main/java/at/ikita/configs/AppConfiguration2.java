package at.ikita.configs;

import at.ikita.model.DayOfWeek;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.Calendar;
import java.util.Date;


/**
 * Configuration class for simple reading from the "local.properties" file.
 *
 * modified to take Calendar.DAY elements instead of DAYOFWEEK enum.
 *
 * @author Fabio.Valentini@student.uibk.ac.at
 * @author lucas.markovic@student.uibk.ac.at
 */

@Configuration
@PropertySource("classpath:/local.properties")
public class AppConfiguration2 {

    @Value("${ikita.logfile}")
    private String logFilePath;

    @Value("${ikita.openingTimeMonday}")
    private String openingTimeMonday;
    @Value("${ikita.openingTimeTuesday}")
    private String openingTimeTuesday;
    @Value("${ikita.openingTimeWednesday}")
    private String openingTimeWednesday;
    @Value("${ikita.openingTimeThursday}")
    private String openingTimeThurday;
    @Value("${ikita.openingTimeFriday}")
    private String openingTimeFriday;
    @Value("${ikita.openingTimeSaturday}")
    private String openingTimeSaturday;
    @Value("${ikita.openingTimeSunday}")
    private String openingTimeSunday;

    @Value("${ikita.closingTimeMonday}")
    private String closingTimeMonday;
    @Value("${ikita.closingTimeTuesday}")
    private String closingTimeTuesday;
    @Value("${ikita.closingTimeWednesday}")
    private String closingTimeWednesday;
    @Value("${ikita.closingTimeThursday}")
    private String closingTimeThurday;
    @Value("${ikita.closingTimeFriday}")
    private String closingTimeFriday;
    @Value("${ikita.closingTimeSaturday}")
    private String closingTimeSaturday;
    @Value("${ikita.closingTimeSunday}")
    private String closingTimeSunday;


    //public AppConfiguration2(){}


    public String getLogFilePath() {
        return logFilePath;
    }

    public LocalTime getOpeningTime(int dayOfWeek) {
        switch (dayOfWeek) {
            case Calendar.MONDAY:
                return LocalTime.parse(openingTimeMonday);
            case Calendar.TUESDAY:
                return LocalTime.parse(openingTimeTuesday);
            case Calendar.WEDNESDAY:
                return LocalTime.parse(openingTimeWednesday);
            case Calendar.THURSDAY:
                return LocalTime.parse(openingTimeThurday);
            case Calendar.FRIDAY:
                return LocalTime.parse(openingTimeFriday);
            case Calendar.SATURDAY:
                return LocalTime.parse(openingTimeSaturday);
            case Calendar.SUNDAY:
                return LocalTime.parse(openingTimeSunday);
            default:
                throw new IllegalArgumentException();
        }
    }

    public LocalTime getClosingTime(int dayOfWeek) {
        switch (dayOfWeek) {
            case Calendar.MONDAY:
                return LocalTime.parse(closingTimeMonday);
            case Calendar.TUESDAY:
                return LocalTime.parse(closingTimeTuesday);
            case Calendar.WEDNESDAY:
                return LocalTime.parse(closingTimeWednesday);
            case Calendar.THURSDAY:
                return LocalTime.parse(closingTimeThurday);
            case Calendar.FRIDAY:
                return LocalTime.parse(closingTimeFriday);
            case Calendar.SATURDAY:
                return LocalTime.parse(closingTimeSaturday);
            case Calendar.SUNDAY:
                return LocalTime.parse(closingTimeSunday);
            default:
                throw new IllegalArgumentException();
        }
    }

    public static Date dateFromLocalTime(LocalTime time) {
        return dateFromLocalTime(time, new Date());
    }

    public static Date dateFromLocalTime(LocalTime time, Date day) {
        LocalDate date = LocalDate.of(day.getYear(), day.getMonth(), day.getDay());
        return Date.from(time.atDate(date).toInstant(ZoneOffset.ofHours(0)));
    }

    public static LocalTime localTimeFromDate(Date time) {
        return LocalTime.of(time.getHours(), time.getMinutes(), time.getSeconds());
    }

}
