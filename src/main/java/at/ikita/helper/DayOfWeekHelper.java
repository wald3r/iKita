package at.ikita.helper;

import java.util.Calendar;

import at.ikita.model.DayOfWeek;


public class DayOfWeekHelper {

    public static final String MONDAY = "Montag";
    public static final String TUESDAY = "Dienstag";
    public static final String WEDNESDAY = "Mittwoch";
    public static final String THURSDAY = "Donnerstag";
    public static final String FRIDAY = "Freitag";
    public static final String SATURDAY = "Samstag";
    public static final String SUNDAY = "Sonntag";

    public static String translateEnumToString(DayOfWeek day) {
        switch (day) {
            case MONDAY:
                return MONDAY;
            case TUESDAY:
                return TUESDAY;
            case WEDNESDAY:
                return WEDNESDAY;
            case THURSDAY:
                return THURSDAY;
            case FRIDAY:
                return FRIDAY;
            case SATURDAY:
                return SATURDAY;
            case SUNDAY:
                return SUNDAY;
            default:
                throw new IllegalArgumentException("Unexpected Day of the Week");
        }
    }


    public static DayOfWeek translateCalendarToEnum(Calendar calendar) {
        int day = calendar.get(Calendar.DAY_OF_WEEK);

        switch (day) {
            case Calendar.SUNDAY:
                return DayOfWeek.SUNDAY;
            case Calendar.MONDAY:
                return DayOfWeek.MONDAY;
            case Calendar.TUESDAY:
                return DayOfWeek.TUESDAY;
            case Calendar.WEDNESDAY:
                return DayOfWeek.WEDNESDAY;
            case Calendar.THURSDAY:
                return DayOfWeek.THURSDAY;
            case Calendar.FRIDAY:
                return DayOfWeek.FRIDAY;
            case Calendar.SATURDAY:
                return DayOfWeek.SATURDAY;
            default:
                throw new IllegalArgumentException("Unexpected Day of the Week");
        }
    }
}
