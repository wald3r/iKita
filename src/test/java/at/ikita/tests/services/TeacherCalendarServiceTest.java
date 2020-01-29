package at.ikita.tests.services;

import java.util.*;

import at.ikita.Main;
import at.ikita.model.Person;
import at.ikita.model.TeacherCalendar;
import at.ikita.services.PersonService;
import at.ikita.services.TeacherCalendarService;

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
 * Tests for {@link TeacherCalendarService}
 *
 * @author Fabio.Valentini@student.uibk.ac.at
 */

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Main.class)
@WebAppConfiguration
public class TeacherCalendarServiceTest {

    @Autowired
    private TeacherCalendarService teacherCalendarService;

    @Autowired
    private PersonService personService;

    private TeacherCalendar testCalendarNew1;
    private TeacherCalendar testCalendarNew2;
    private TeacherCalendar testCalendarDuplicate;


    @Before
    public void setupTestData() {
        testCalendarNew1 = TeacherCalendar.create(
                new GregorianCalendar(2017, 6, 1).getTime(),
                personService.loadEntry(10));

        testCalendarNew2 = TeacherCalendar.create(
                new GregorianCalendar(2017, 6, 2).getTime(),
                personService.loadEntry(11));

        TeacherCalendar temp = teacherCalendarService.loadEntry(1);
        testCalendarDuplicate = TeacherCalendar.create(
                temp.getDate(), temp.getTeacher());
    }

    @Test
    @WithMockUser(username = "admin@ikita.at", authorities = {"ADMIN"})
    public void testLoadEntry() {
        TeacherCalendar entry = teacherCalendarService.loadEntry(1);
        Assert.assertNotNull(entry);
    }

    @Test
    @WithMockUser(username = "admin@ikita.at", authorities = {"ADMIN"})
    public void testLoadByDate() {
        Date queryDate = new GregorianCalendar(2017, 5, 10).getTime();
        Collection<TeacherCalendar> entries = teacherCalendarService.loadByDate(queryDate);

        for (TeacherCalendar entry : entries)
            Assert.assertEquals(queryDate, entry.getDate());
    }

    @Test
    @WithMockUser(username = "admin@ikita.at", authorities = {"ADMIN"})
    public void testLoadByTeacher() {
        Person queryTeacher;
        Collection<TeacherCalendar> entries;

        // Test with Teacher 1
        queryTeacher = personService.loadByEmail("teacher1@ikita.at");
        entries = teacherCalendarService.loadByTeacher(queryTeacher);

        for (TeacherCalendar entry : entries)
            Assert.assertEquals(queryTeacher, entry.getTeacher());

        // Test with Teacher 2
        queryTeacher = personService.loadByEmail("teacher2@ikita.at");
        entries = teacherCalendarService.loadByTeacher(queryTeacher);

        for (TeacherCalendar entry : entries)
            Assert.assertEquals(queryTeacher, entry.getTeacher());
    }

    @Test
    @DirtiesContext
    @WithMockUser(username = "admin@ikita.at", authorities = {"ADMIN"})
    public void testSaveEntry() {
        TeacherCalendar entry = teacherCalendarService.saveEntry(testCalendarNew1);
        Assert.assertTrue(testCalendarNew1.equals(entry));
        Assert.assertNotEquals(0, (long) entry.getId());
    }

    @Test
    @DirtiesContext
    @WithMockUser(username = "admin@ikita.at", authorities = {"ADMIN"})
    public void testDeleteEntry() {
        TeacherCalendar entry = teacherCalendarService.loadEntry(1);
        Assert.assertNotNull(entry);

        teacherCalendarService.deleteEntry(entry);

        entry = teacherCalendarService.loadEntry(1);
        Assert.assertNull(entry);
    }

    @Test
    @DirtiesContext
    @WithMockUser(username = "admin@ikita.at", authorities = {"ADMIN"})
    public void testUpdateEntry() {
        TeacherCalendar entry = teacherCalendarService.loadEntry(2);
        Assert.assertNotNull(entry);

        boolean newLunch = !entry.hasLunch();

        entry.setLunch(newLunch);

        TeacherCalendar newEntry = teacherCalendarService.saveEntry(entry);

        Assert.assertEquals(newLunch, newEntry.hasLunch());

        Assert.assertTrue(entry.equals(newEntry));
    }

    @Test
    @DirtiesContext
    @WithMockUser(username = "admin@ikita.at", authorities = {"ADMIN"})
    public void testNumberSanity() {
        Collection<TeacherCalendar> state0 = teacherCalendarService.loadAll();
        teacherCalendarService.saveEntry(testCalendarNew2);
        Collection<TeacherCalendar> state1 = teacherCalendarService.loadAll();
        teacherCalendarService.deleteEntry(testCalendarNew2);
        Collection<TeacherCalendar> state2 = teacherCalendarService.loadAll();

        Assert.assertEquals(state0.size(), state1.size() - 1);
        Assert.assertEquals(state1.size(), state2.size() + 1);
        Assert.assertEquals(state0.size(), state2.size());
    }

    @Test
    @WithMockUser(username = "admin@ikita.at", authorities = {"ADMIN"})
    public void failLoadEntry() {
        TeacherCalendar entry = teacherCalendarService.loadEntry(99);
        Assert.assertNull(entry);
    }

    @Test(expected = org.springframework.dao.DataIntegrityViolationException.class)
    @WithMockUser(username = "admin@ikita.at", authorities = {"ADMIN"})
    public void failSaveEntry() {
        teacherCalendarService.saveEntry(testCalendarDuplicate);
    }

}
