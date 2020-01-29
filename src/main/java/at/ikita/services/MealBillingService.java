package at.ikita.services;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import at.ikita.model.Person;
import at.ikita.model.PersonRole;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;


/**
 * Service for sending meal bills to parents.
 *
 * @author Fabio.Valentini@student.uibk.ac.at
 */

@Component
@Scope("application")
public class MealBillingService {

    private PersonService personService;
    private MealCalculatorService mealCalculatorService;
    private MailService mailService;

    @Autowired
    public MealBillingService(PersonService personService,
                              MealCalculatorService mealCalculatorService,
                              MailService mailService) {
        Assert.notNull(personService);
        Assert.notNull(mealCalculatorService);
        Assert.notNull(mailService);

        this.personService = personService;
        this.mealCalculatorService = mealCalculatorService;
        this.mailService = mailService;
    }

    private void sendBillingMailForPerson(Person person, Date since, Date before) {
        double price = mealCalculatorService.getMealPriceAllChildren(person, since, before);

        if (price == 0.0)
            return;

        StringBuilder mailText = new StringBuilder();

        mailText.append(String.format("Liebe/r %s %s,\n\n",
                person.getFirstName(), person.getLastName()));

        mailText.append("Die folgenden Essen wurden konsumiert:\n");

        for (String entry : mealCalculatorService.getMealListAllChildren(person, since, before)) {
            mailText.append(entry);
            mailText.append("\n");
        }

        mailText.append("\n");

        mailText.append("Die folgenden Kosten für Mittagessen sind daher beim Personal zu begleichen:\n");
        mailText.append(String.format("%f\n\n", price));
        mailText.append("Mit freundlichen Grüßen\n");
        mailText.append("Dein iKiTa Team");

        mailService.sendSingleMail(person.getEmail(),
                String.format("ikita Essensrechnung (%s - %s)",
                        new SimpleDateFormat("dd. MMMM YYYY").format(since),
                        new SimpleDateFormat("dd. MMMM YYYY").format(before)),
                mailText.toString());
    }

    private void sendMealBills(Date since, Date before) {
        for (Person person : personService.loadAllByRole(PersonRole.PARENT)) {
            sendBillingMailForPerson(person, since, before);
        }
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

    public void sendMealBillsForLastWeek() {
        Calendar firstDay = getStartOfWeek();
        Calendar lastDay = getStartOfWeek();

        lastDay.add(Calendar.DAY_OF_MONTH, 6);

        sendMealBills(firstDay.getTime(), lastDay.getTime());
    }
}
