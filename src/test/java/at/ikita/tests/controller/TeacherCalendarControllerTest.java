package at.ikita.tests.controller;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Calendar;
import java.util.Collection;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import at.ikita.Main;
import at.ikita.model.Child;
import at.ikita.model.ChildCalendar;
import at.ikita.model.Person;
import at.ikita.model.PersonRole;
import at.ikita.model.TeacherCalendar;
import at.ikita.services.ChildCalendarService;
import at.ikita.services.ChildService;
import at.ikita.services.PersonService;
import at.ikita.services.TeacherCalendarService;
import at.ikita.ui.controllers.TeacherCalendarController;

/**
 * Tests for TeacherCalendarController
 * 
 * @author Andre.Potocnik@student.uibk.ac.at
 *
 */
@SuppressWarnings("deprecation")
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Main.class)
@WebAppConfiguration
public class TeacherCalendarControllerTest {
    
    @Autowired 
    private TeacherCalendarController teacherCalendarController;
    
    @Autowired
    private ChildService childService;
    
    @Autowired
    private ChildCalendarService childCalendarService;
    
    @Autowired
    private TeacherCalendarService teacherCalendarService;
    
    @Autowired
    private PersonService personService;
    
    private Date testDay;
    
    private ChildCalendar testChildCalendar1;
    private ChildCalendar testChildCalendar2;
    private TeacherCalendar testTeacherCalendar1;
    private TeacherCalendar testTeacherCalendar2;
    
    
    
    
    @Before
    public void setupTestData() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(2016, 4, 8);
        testDay = calendar.getTime();
        
        Child testChild1 = childService.loadEntry(1);
        Child testChild2 = childService.loadEntry(2);
        
        testChildCalendar1 = ChildCalendar.create(testDay, testChild1);
        testChildCalendar2 = ChildCalendar.create(testDay, testChild2);
        
        Person testTeacher1 = personService.loadEntry(41);
        Person testTeacher2 = personService.loadEntry(42);
        
        testTeacherCalendar1 = TeacherCalendar.create(testDay, testTeacher1);
        testTeacherCalendar2 = TeacherCalendar.create(testDay, testTeacher2);
    }
    
    @Test
    @DirtiesContext
    @WithMockUser(username = "teacher1@ikita.at", authorities = {"TEACHER"})
    public void testCountPresentChildrenByDate() {
        childCalendarService.saveEntry(testChildCalendar1);
        childCalendarService.saveEntry(testChildCalendar2);
        
        int testPresentChildren = teacherCalendarController.countPresentChildrenByDate(testDay);
        Assert.assertEquals(2, testPresentChildren);
    }
    
    @Test
    @DirtiesContext
    @WithMockUser(username = "teacher1@ikita.at", authorities = {"TEACHER"})
    public void testCountPresentTeacherByDate() {
        teacherCalendarService.saveEntry(testTeacherCalendar1);
        teacherCalendarService.saveEntry(testTeacherCalendar2);
        
        int testPresentTeacher = teacherCalendarController.countPresentTeacherByDate(testDay);
        Assert.assertEquals(2, testPresentTeacher);
    }
    
    @Test
    @DirtiesContext
    @WithMockUser(username = "teacher1@ikita.at", authorities = {"TEACHER"})
    public void testCountChildrenMealsByDate() {
        testChildCalendar1.setLunch(true);
        childCalendarService.saveEntry(testChildCalendar1);
        childCalendarService.saveEntry(testChildCalendar2);
        
        int testAmountChildrenMeals = teacherCalendarController.countChildrenMealsByDate(testDay);
        
        Assert.assertEquals(1, testAmountChildrenMeals);
    }
    
    @Test
    @DirtiesContext
    @WithMockUser(username = "teacher1@ikita.at", authorities = {"TEACHER"})
    public void testCountTeacherMealsByDate() {
        testTeacherCalendar1.setLunch(true);
        teacherCalendarService.saveEntry(testTeacherCalendar1);
        teacherCalendarService.saveEntry(testTeacherCalendar2);
        
        int testAmountTeacherMeals = teacherCalendarController.countTeacherMealsByDate(testDay);
        
        Assert.assertEquals(1, testAmountTeacherMeals);
    }
    
    @Test
    @DirtiesContext
    @WithMockUser(username = "teacher1@ikita.at", authorities = {"TEACHER"})
    public void testGetPresentChildrenByDate() {
        childCalendarService.saveEntry(testChildCalendar1);
        childCalendarService.saveEntry(testChildCalendar2);
        
        List<ChildCalendar> testPresentChildren = teacherCalendarController.getPresentChildrenByDate(testDay);
        
        Assert.assertEquals(2, testPresentChildren.size());
        Assert.assertTrue(testPresentChildren.contains(testChildCalendar1));
        Assert.assertTrue(testPresentChildren.contains(testChildCalendar2));
    }
    
    @Test
    @DirtiesContext
    @WithMockUser(username = "teacher1@ikita.at", authorities = {"TEACHER"})
    public void testGetPresentTeacherByDate() {
        teacherCalendarService.saveEntry(testTeacherCalendar1);
        teacherCalendarService.saveEntry(testTeacherCalendar2);
        
        List<TeacherCalendar> testPresentTeacher = teacherCalendarController.getPresentTeacherByDate(testDay);
        
        Assert.assertEquals(2, testPresentTeacher.size());
        Assert.assertTrue(testPresentTeacher.contains(testTeacherCalendar1));
        Assert.assertTrue(testPresentTeacher.contains(testTeacherCalendar2));
    }
    
    @Test
    @DirtiesContext
    @WithMockUser(username = "teacher1@ikita.at", authorities = {"TEACHER"})
    public void testDoSaveTeacherCalendar() {
        teacherCalendarService.saveEntry(testTeacherCalendar1);
        int testTeacheCalendarId = testTeacherCalendar1.getId();
        
        TeacherCalendar testFetchedTeacherCalender1 = teacherCalendarService.loadEntry(testTeacheCalendarId);
        
        Assert.assertTrue(testFetchedTeacherCalender1.equals(testTeacherCalendar1));        
    }
    
    @Test
    @DirtiesContext
    @WithMockUser(username = "teacher1@ikita.at", authorities = {"TEACHER"})
    public void testGetAllTeacher() {
        Map<String, Person> testAllTeacher = teacherCalendarController.getAllTeacher();
        Collection<Person> testAllTeacherCollection = testAllTeacher.values();
        
        Assert.assertEquals(2, testAllTeacher.size());
        
        for(Person teacher : testAllTeacherCollection) {
            Assert.assertTrue(teacher.getRoles().contains(PersonRole.TEACHER));
        }
    }
}
