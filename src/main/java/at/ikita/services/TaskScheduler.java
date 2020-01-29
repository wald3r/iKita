package at.ikita.services;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/**
 * Service that automatically executes tasks on 
 * a given date
 * 
 * @author Andre.Potocnik@student.uibk.ac.at
 * @author Fabio.Valentini@student.uibk.ac.at
 *
 */
@Service
@EnableScheduling
public class TaskScheduler {
    
    @Autowired
    private LunchRemainderService lunchRemainderService;

    @Autowired
    private PersonDeactivationService personDeactivationService;

    @Autowired
    private DayCareCalendarMaintenanceService dayCareCalendarMaintenanceService;

    @Autowired
    private MealBillingService mealBillingService;

    /**
     * Send E-Mail notification on every Friday to
     * parents which allow to receive lunch reminder
     * notifications
     */
    @Scheduled(cron = "0 0 7 * * FRI")
    private void lunchReminderScheduler() {
        lunchRemainderService.checkCandidateChildren(new Date());
    }

    /**
     * Check if parents have active children every day, and set
     * the Person.active attribute accordingly.
     */
    @Scheduled(cron = "0 0 0 * * *")
    private void personDeactivationScheduler() {
        personDeactivationService.updatePersonStatus();
    }

    /**
     * Create next week's calendar entries from default values
     * (if it hasn't been done via the GUI).
     */
    @Scheduled(cron = "0 30 23 * * SUN")
    private void dayCareCalendarMaintenanceScheduler() {
        dayCareCalendarMaintenanceService.updateNextWeek();
    }

    /**
     * Send regular meal billing E-Mails for parents where the price is not 0.
     */
    @Scheduled(cron = "0 0 23 * * SUN")
    private void mealBillingScheduler() {
        mealBillingService.sendMealBillsForLastWeek();
    }
}
