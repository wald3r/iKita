package at.ikita.tests.services;

import java.util.*;

import at.ikita.Main;
import at.ikita.model.Child;
import at.ikita.model.ChildCalendar;
import at.ikita.services.ChildCalendarService;
import at.ikita.services.ChildService;

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
 * Tests for {@link ChildCalendarService}
 *
 * @author Fabio.Valentini@student.uibk.ac.at
 */

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Main.class)
@WebAppConfiguration
public class ChildCalendarServiceTest {

    @Autowired
    private ChildCalendarService childCalendarService;

    @Autowired
    private ChildService childService;

    private Child testChild;
    private Date testDate;

    private ChildCalendar testCalendarNew1;
    private ChildCalendar testCalendarNew2;
    private ChildCalendar testCalendarDuplicate;


    @Before
    public void setupTestData() {
        testDate = new GregorianCalendar(2017, 4, 23).getTime();
        testChild = childService.loadEntry(5);
        ChildCalendar temp = childCalendarService.loadEntry(1);

        Date testDate1 = new GregorianCalendar(2017, 7, 24).getTime();
        Date testDate2 = new GregorianCalendar(2017, 7, 26).getTime();

        Child testChild1 = childService.loadEntry(1);
        Child testChild2 = childService.loadEntry(2);

        testCalendarNew1 = ChildCalendar.create(testDate1, testChild1);
        testCalendarNew2 = ChildCalendar.create(testDate2, testChild2);
        testCalendarDuplicate = ChildCalendar.create(temp.getDate(), temp.getChild());

        testCalendarNew1.setLunch(true);
        testCalendarNew2.setLunch(true);
        testCalendarDuplicate.setLunch(true);
    }

    @Test
    @WithMockUser(username = "admin@ikita.at", authorities = {"ADMIN"})
    public void testLoadEntry() {
        ChildCalendar entry = childCalendarService.loadByChildAndDate(testChild, testDate);
        Assert.assertNotNull(entry);
    }

    @Test
    @WithMockUser(username = "admin@ikita.at", authorities = {"ADMIN"})
    public void testLoadEntryByDate() {
        Collection<ChildCalendar> entries = childCalendarService.loadByDate(
                new GregorianCalendar(2017, 4, 2).getTime());

        Assert.assertEquals(12, entries.size());
    }

    @Test
    @WithMockUser(username = "admin@ikita.at", authorities = {"ADMIN"})
    public void testLoadEntryByChild() {
        Collection<Child> children = childService.loadAll();

        for (Child child : children) {
            Collection<ChildCalendar> entries = childCalendarService.loadByChild(child);
            for (ChildCalendar entry : entries)
                Assert.assertEquals(child, entry.getChild());
        }
    }

    @Test
    @DirtiesContext
    @WithMockUser(username = "admin@ikita.at", authorities = {"ADMIN"})
    public void testSaveEntry() {
        ChildCalendar entry = childCalendarService.saveEntry(testCalendarNew1);
        Assert.assertTrue(testCalendarNew1.equals(entry));
        Assert.assertNotEquals(0, (long) entry.getId());
    }

    @Test
    @DirtiesContext
    @WithMockUser(username = "admin@ikita.at", authorities = {"ADMIN"})
    public void testDeleteEntry() {
        ChildCalendar test = childCalendarService.loadEntry(1);
        Assert.assertNotNull(test);

        childCalendarService.deleteEntry(test);

        test = childCalendarService.loadEntry(1);
        Assert.assertNull(test);
    }

    @Test
    @DirtiesContext
    @WithMockUser(username = "admin@ikita.at", authorities = {"ADMIN"})
    public void testUpdateEntry() {
        ChildCalendar entry = childCalendarService.loadEntry(2);
        Assert.assertNotNull(entry);

        entry.setLunch(false);

        ChildCalendar newEntry = childCalendarService.saveEntry(entry);

        Assert.assertEquals(false, newEntry.hasLunch());

        Assert.assertTrue(entry.equals(newEntry));
    }

    @Test
    @DirtiesContext
    @WithMockUser(username = "admin@ikita.at", authorities = {"ADMIN"})
    public void testNumberSanity() {
        Collection<ChildCalendar> state0 = childCalendarService.loadAll();
        childCalendarService.saveEntry(testCalendarNew2);
        Collection<ChildCalendar> state1 = childCalendarService.loadAll();
        childCalendarService.deleteEntry(testCalendarNew2);
        Collection<ChildCalendar> state2 = childCalendarService.loadAll();

        Assert.assertEquals(state0.size() + 1, state1.size());
        Assert.assertEquals(state2.size() + 1, state1.size());
        Assert.assertEquals(state0.size(), state2.size());
    }

    @Test
    @WithMockUser(username = "admin@ikita.at", authorities = {"ADMIN"})
    public void failLoadEntry() {
        ChildCalendar entry = childCalendarService.loadByChildAndDate(
                childService.loadEntry(1),
                new GregorianCalendar(2000, 0, 1).getTime());

        Assert.assertNull(entry);
    }

    @Test(expected = org.springframework.dao.DataIntegrityViolationException.class)
    @WithMockUser(username = "admin@ikita.at", authorities = {"ADMIN"})
    public void failSaveEntry() {
        childCalendarService.saveEntry(testCalendarDuplicate);
    }

}
