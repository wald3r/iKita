package at.ikita.tests.controller;

import at.ikita.Main;
import at.ikita.model.DayCareCalendar;
import at.ikita.services.DayCareCalendarService;
import at.ikita.ui.controllers.ConfigurationController2;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.Date;

/**
 * Tests for {@link ConfigurationController2}
 *
 * @author lucas.markovic@student.uibk.ac.at
 */

@SuppressWarnings("deprecation")
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Main.class)
@WebAppConfiguration
public class ConfigurationController2Test {

    @Autowired
    ConfigurationController2 configController;
    @Autowired
    DayCareCalendarService dayCareCalendarService;


    @Test
    public void testDefaultTimesDaycare()
    {
        Date date = new Date(2099, 1, 2);

        Assert.assertEquals(configController.getMinHourDayCare(date), 8);
        Assert.assertEquals(configController.getMinMinuteDayCare(date), 0);
        Assert.assertEquals(configController.getMaxHourDayCare(date), 18);
        Assert.assertEquals(configController.getMaxMinuteDayCare(date), 0);
    }

    @Test
    @DirtiesContext
    public void testTimeDaycareCalendar()
    {
        Date date = new Date(2030, 3, 4);
        DayCareCalendar dayCareCalendar = DayCareCalendar.create(date);
        dayCareCalendar.setMealPrice(3.14);
        dayCareCalendar.setMeal1Description("asdf");
        dayCareCalendar.setMeal2Description("möp");

        Date dateOpen = new Date(2030, 3, 4);
        dateOpen.setHours(9);
        dateOpen.setMinutes(15);
        dayCareCalendar.setOpeningTime(dateOpen);

        Date dateClose = new Date(2030, 3, 4);
        dateClose.setHours(15);
        dateClose.setMinutes(30);
        dayCareCalendar.setClosingTime(dateClose);
        dayCareCalendarService.saveEntry(dayCareCalendar);

        Assert.assertEquals(configController.getMinHourDayCare(date), 9);
        Assert.assertEquals(configController.getMinMinuteDayCare(date), 15);
        Assert.assertEquals(configController.getMaxHourDayCare(date), 15);
        Assert.assertEquals(configController.getMaxMinuteDayCare(date), 30);
    }

}
