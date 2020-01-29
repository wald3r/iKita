package at.ikita.configs;

import java.time.*;
import java.util.Date;

import at.ikita.model.DayOfWeek;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;


/**
 * Configuration class for simple reading from the "local.properties" file.
 *
 * @author Fabio.Valentini@student.uibk.ac.at
 */

@Configuration
@PropertySource("classpath:/local.properties")
public class AppConfiguration {

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


    public String getLogFilePath() {
        return logFilePath;
    }

    public LocalTime getOpeningTime(DayOfWeek dayOfWeek) {
        switch (dayOfWeek) {
            case MONDAY:
                return LocalTime.parse(openingTimeMonday);
            case TUESDAY:
                return LocalTime.parse(openingTimeTuesday);
            case WEDNESDAY:
                return LocalTime.parse(openingTimeWednesday);
            case THURSDAY:
                return LocalTime.parse(openingTimeThurday);
            case FRIDAY:
                return LocalTime.parse(openingTimeFriday);
            case SATURDAY:
                return LocalTime.parse(openingTimeSaturday);
            case SUNDAY:
                return LocalTime.parse(openingTimeSunday);
            default:
                throw new IllegalArgumentException();
        }
    }

    public LocalTime getClosingTime(DayOfWeek dayOfWeek) {
        switch (dayOfWeek) {
            case MONDAY:
                return LocalTime.parse(closingTimeMonday);
            case TUESDAY:
                return LocalTime.parse(closingTimeTuesday);
            case WEDNESDAY:
                return LocalTime.parse(closingTimeWednesday);
            case THURSDAY:
                return LocalTime.parse(closingTimeThurday);
            case FRIDAY:
                return LocalTime.parse(closingTimeFriday);
            case SATURDAY:
                return LocalTime.parse(closingTimeSaturday);
            case SUNDAY:
                return LocalTime.parse(closingTimeSunday);
            default:
                throw new IllegalArgumentException();
        }
    }

    public static Date dateFromLocalTime(LocalTime time) {
        return dateFromLocalTime(time, new Date());
    }

    public static Date dateFromLocalTime(LocalTime time, Date day) {
        LocalDate date = LocalDate.of(day.getYear() + 1900, day.getMonth() + 1, day.getDate());
        LocalDateTime dateTime = time.atDate(date);
        return Date.from(dateTime.toInstant(ZoneId.systemDefault().getRules().getOffset(dateTime)));
    }

    public static LocalTime localTimeFromDate(Date time) {
        return LocalTime.of(time.getHours(), time.getMinutes(), time.getSeconds());
    }

}
