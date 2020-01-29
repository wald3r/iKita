package at.ikita.services;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import at.ikita.model.Child;
import at.ikita.model.ChildCalendar;

import at.ikita.model.Person;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;


/**
 * Service for calculating meal prices.
 *
 * @author Fabio.Valentini@student.uibk.ac.at
 */

@Component
@Scope("application")
public class MealCalculatorService {

    private ChildCalendarService childCalendarService;
    private DayCareCalendarService dayCareCalendarService;

    public MealCalculatorService(ChildCalendarService childCalendarService,
                                 DayCareCalendarService dayCareCalendarService) {
        Assert.notNull(childCalendarService);
        Assert.notNull(dayCareCalendarService);

        this.childCalendarService = childCalendarService;
        this.dayCareCalendarService = dayCareCalendarService;
    }

    private double getMealPriceOneChild(Child child, Date since, Date before) {
        double price = 0.0;

        List<ChildCalendar> entries = childCalendarService.loadByChildAndDateRange(
                child, since, before);

        for (ChildCalendar entry : entries)
            if (entry.isLunch())
                price += dayCareCalendarService.loadByDate(entry.getDate()).getMealPrice();

        return price;
    }

    List<String> getMealListAllChildren(Person person, Date since, Date before) {
        List<String> mealList = new ArrayList<>();

        for (Child child : person.getChildren()) {
            List<ChildCalendar> entries = childCalendarService.loadByChildAndDateRange(child, since, before);

            for (ChildCalendar entry : entries) {
                mealList.add(String.format("%s, %s %s: %.2f",
                        new SimpleDateFormat("dd. MMMM YYYY").format(entry.getDate()),
                        entry.getChild().getFirstName(),
                        entry.getChild().getLastName(),
                        dayCareCalendarService.loadByDate(entry.getDate()).getMealPrice()));
            }
        }

        return mealList;
    }

    double getMealPriceAllChildren(Person person, Date since, Date before) {
        double price = 0.0;

        for (Child child : person.getChildren()) {
            price += getMealPriceOneChild(child, since, before);
        }

        return price;
    }
}
