package at.ikita.services;

import java.time.LocalTime;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import at.ikita.configs.AppConfiguration;
import at.ikita.model.DayCareCalendar;
import at.ikita.model.DayOfWeek;

import at.ikita.model.LogMessageType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;


/**
 * Service that creates DayCareCalendar entries for the next week (if it is not done via GUI)
 *
 * @author Fabio.Valentini@student.uibk.ac.at
 */

@Component
@Scope("application")
public class DayCareCalendarMaintenanceService {

    private DayCareCalendarService dayCareCalendarService;
    private AppConfiguration appConfiguration;
    private LogService logService;

    @Autowired
    public DayCareCalendarMaintenanceService(DayCareCalendarService dayCareCalendarService,
                                             AppConfiguration appConfiguration,
                                             LogService logService) {
        Assert.notNull(dayCareCalendarService);
        Assert.notNull(appConfiguration);
        Assert.notNull(logService);

        this.dayCareCalendarService = dayCareCalendarService;
        this.appConfiguration = appConfiguration;
        this.logService = logService;
    }

    private boolean doesEntryExistFor(Calendar date) {
        return dayCareCalendarService.loadByDate(date.getTime()) != null;
    }

    private static Calendar dayOffset(Calendar date, int offset) {
        date.add(Calendar.DAY_OF_MONTH, offset);
        return date;
    }

    private static Calendar getStartOfWeek() {
        return getStartOfWeek(GregorianCalendar.getInstance());
    }

    private static Calendar getStartOfWeek(Calendar date) {
        return dayOffset(date, date.get(Calendar.DAY_OF_WEEK) - 7);
    }

    private static DayOfWeek offsetToDayOfWeek(int offset) {
        switch (offset) {
            case 0:
                return DayOfWeek.MONDAY;
            case 1:
                return DayOfWeek.TUESDAY;
            case 2:
                return DayOfWeek.WEDNESDAY;
            case 3:
                return DayOfWeek.THURSDAY;
            case 4:
                return DayOfWeek.FRIDAY;
            case 5:
                return DayOfWeek.SATURDAY;
            case 6:
                return DayOfWeek.SUNDAY;
            default:
                throw new IllegalArgumentException();
        }
    }

    public void updateNextWeek() {
        // check if there are already entries for next week
        DayCareCalendar lastEntry = dayCareCalendarService.loadLastEntry();

        if (lastEntry.getDate().after(new Date())) {
            logService.log(LogMessageType.INFORMATION, this.getClass(),
                    "At least one DayCareCalendar entry for next week already exists.");
            logService.log(LogMessageType.INFORMATION, this.getClass(),
                    "Skipping Auto-Create of new entries.");
            return;
        }

        Calendar startOfWeek = getStartOfWeek();

        // iterate over the week following this one
        for (int offset = 0; offset < 7; offset++) {
            // check if an entry already exists
            Calendar newDate = dayOffset(startOfWeek, 7 + offset);
            boolean entryExists = doesEntryExistFor(newDate);

            if (!entryExists) {
                // check if the DayCareCenter is supposed to be open on that day
                LocalTime openingTime = appConfiguration.getOpeningTime(offsetToDayOfWeek(offset));
                LocalTime closingTime = appConfiguration.getClosingTime(offsetToDayOfWeek(offset));

                // if both opening and closing time are "00:00", skip creating a new entry
                if (openingTime.equals(LocalTime.parse("00:00")) && closingTime.equals(LocalTime.parse("00:00")))
                    continue;

                logService.log(LogMessageType.INFORMATION, this.getClass(),
                        "Creating DayCareCalendar entry for " + newDate.getTime().toString() + " from default values.");

                DayCareCalendar newEntry = DayCareCalendar.create(newDate.getTime());
                newEntry.setOpeningTime(AppConfiguration.dateFromLocalTime(openingTime));
                newEntry.setClosingTime(AppConfiguration.dateFromLocalTime(closingTime));
                newEntry.setMeal1Description("Platzhalter");
                newEntry.setMeal2Description("Platzhalter");
                newEntry.setMealPrice(0.0);
                dayCareCalendarService.saveEntry(newEntry);
            }
        }
    }
}
