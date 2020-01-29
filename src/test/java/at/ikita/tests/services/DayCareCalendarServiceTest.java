package at.ikita.tests.services;

import java.util.*;

import at.ikita.Main;
import at.ikita.model.DayCareCalendar;
import at.ikita.services.DayCareCalendarService;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;


/**
 * Tests for {@link DayCareCalendarService}
 *
 * @author Fabio.Valentini@student.uibk.ac.at
 */

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Main.class)
@WebAppConfiguration
public class DayCareCalendarServiceTest {

    @Autowired
    private DayCareCalendarService dayCareCalendarService;

    private DayCareCalendar testCalendarNew1;
    private DayCareCalendar testCalendarNew2;
    private DayCareCalendar testCalendarDuplicate;


    @Before
    public void setupTestData() {
        Date testDate1 = new GregorianCalendar(2000, 0, 1).getTime();
        Date testDate2 = new GregorianCalendar(2000, 0, 2).getTime();
        Date testDate3 = new GregorianCalendar(2017, 3, 10).getTime();

        testCalendarNew1 = DayCareCalendar.create(testDate1);
        testCalendarNew2 = DayCareCalendar.create(testDate2);
        testCalendarDuplicate = DayCareCalendar.create(testDate3);

        testCalendarNew1.setOpeningTime(new Date(2000, 1, 1, 8, 0, 0));
        testCalendarNew2.setOpeningTime(new Date(2000, 1, 1, 8, 0, 0));
        testCalendarDuplicate.setOpeningTime(new Date(2000, 1, 1, 8, 0, 0));

        testCalendarNew1.setClosingTime(new Date(2000, 1, 1, 17, 0, 0));
        testCalendarNew2.setClosingTime(new Date(2000, 1, 1, 17, 0, 0));
        testCalendarDuplicate.setClosingTime(new Date(2000, 1, 1, 17, 0, 0));

        testCalendarNew1.setMealPrice(3.14);
        testCalendarNew2.setMealPrice(3.14);
        testCalendarDuplicate.setMealPrice(3.14);

        testCalendarNew1.setMeal1Description("Brot und Wasser");
        testCalendarNew2.setMeal1Description("Brot und Wasser");
        testCalendarDuplicate.setMeal1Description("Brot und Wasser");
    }

    @Test
    @WithMockUser(username = "admin@ikita.at", authorities = {"ADMIN"})
    public void testLoadEntry() {
        DayCareCalendar entry = dayCareCalendarService.loadEntry(1);
        Assert.assertNotNull(entry);
    }

    @Test
    @WithMockUser(username = "admin@ikita.at", authorities = {"ADMIN"})
    public void testLoadByDate() {
        DayCareCalendar entry = dayCareCalendarService.loadByDate(
                new GregorianCalendar(2017, 3, 10).getTime());
        Assert.assertNotNull(entry);
    }

    @Test
    @DirtiesContext
    @WithMockUser(username = "admin@ikita.at", authorities = {"ADMIN"})
    public void testSaveEntry() {
        DayCareCalendar entry = dayCareCalendarService.saveEntry(testCalendarNew1);
        Assert.assertTrue(testCalendarNew1.equals(entry));
        Assert.assertNotEquals(0, (long) entry.getId());
    }

    @Test
    @DirtiesContext
    @WithMockUser(username = "admin@ikita.at", authorities = {"ADMIN"})
    public void testDeleteEntry() {
        DayCareCalendar test = dayCareCalendarService.loadEntry(2);
        Assert.assertNotNull(test);

        dayCareCalendarService.deleteEntry(test);

        test = dayCareCalendarService.loadEntry(test.getId());
        Assert.assertNull(test);
    }

    @Test
    @DirtiesContext
    @WithMockUser(username = "admin@ikita.at", authorities = {"ADMIN"})
    public void testUpdateEntry() {
        DayCareCalendar entry = dayCareCalendarService.loadEntry(3);
        Assert.assertNotNull(entry);

        entry.setMeal1Description("Luft und Liebe");

        DayCareCalendar newEntry = dayCareCalendarService.saveEntry(entry);

        Assert.assertEquals("Luft und Liebe", newEntry.getMeal1Description());
        Assert.assertTrue(entry.equals(newEntry));
    }

    @Test
    @DirtiesContext
    @WithMockUser(username = "admin@ikita.at", authorities = {"ADMIN"})
    public void testNumberSanity() {
        Collection<DayCareCalendar> state0 = dayCareCalendarService.loadAll();
        dayCareCalendarService.saveEntry(testCalendarNew2);
        Collection<DayCareCalendar> state1 = dayCareCalendarService.loadAll();
        dayCareCalendarService.deleteEntry(testCalendarNew2);
        Collection<DayCareCalendar> state2 = dayCareCalendarService.loadAll();

        Assert.assertEquals(state0.size() + 1, state1.size());
        Assert.assertEquals(state2.size() + 1, state1.size());
        Assert.assertEquals(state0.size(), state2.size());
    }

    @Test
    @WithMockUser(username = "admin@ikita.at", authorities = {"ADMIN"})
    public void failLoadEntry() {
        DayCareCalendar entry = dayCareCalendarService.loadByDate(
                new GregorianCalendar(2016, 0, 1).getTime());

        Assert.assertNull(entry);
    }

    @Test(expected = org.springframework.dao.DataIntegrityViolationException.class)
    @WithMockUser(username = "admin@ikita.at", authorities = {"ADMIN"})
    public void failSaveEntry() {
        dayCareCalendarService.saveEntry(testCalendarDuplicate);
    }

}
