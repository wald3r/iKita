package at.ikita.tests.services;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Calendar;

import org.junit.runner.RunWith;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import at.ikita.Main;
import at.ikita.model.Person;
import at.ikita.model.TeacherCalendar;
import at.ikita.services.PersonService;
import at.ikita.services.TeacherCalendarService;
import at.ikita.services.TeacherCalendarTeacherService;
import at.ikita.ui.controllers.TeacherCalendarController;


/**
 * Tests for TeacherCalendarTeacherService
 * 
 * @author Andre.Potocnik@student.uibk.ac.at
 *
 */
@SuppressWarnings("deprecation")
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Main.class)
@WebAppConfiguration
public class TeacherCalendarTeacherServiceTest {
    
    @Autowired
    private TeacherCalendarController teacherCalendarController;
    
    @Autowired
    private PersonService personService;
    
    @Autowired
    private TeacherCalendarService teacherCalendarService;
    
    @Autowired
    private TeacherCalendarTeacherService teacherCalendarTeacherService;
    
    private TeacherCalendar testTeacherCalendar1;
     
    
    private Person testTeacher1;
    private Person testTeacher2;
    
    private Date testDay;
        
    @Before
    public void setupTestData() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(2016, 4, 8);
        testDay = calendar.getTime();
        
        testTeacher1 = personService.loadEntry(41); // present teacher
        testTeacher2 = personService.loadEntry(42); // not present teacher
        
        testTeacherCalendar1 = TeacherCalendar.create(testDay, testTeacher1);
    }
    
    @Test
    @DirtiesContext
    @WithMockUser(username = "teacher1@ikita.at", authorities = {"TEACHER"})
    public void testRemovePresentTeacher() {
        teacherCalendarService.saveEntry(testTeacherCalendar1);
        
        List<TeacherCalendar> presentTeacher = teacherCalendarController.getPresentTeacherByDate(testDay);
        Map<String, Person> notPresentTeacherObjects = teacherCalendarTeacherService.removePresentTeacher(presentTeacher);
        
        Assert.assertEquals(1, notPresentTeacherObjects.size());
        Assert.assertTrue(notPresentTeacherObjects.containsValue(testTeacher2));
        Assert.assertFalse(notPresentTeacherObjects.containsValue(testTeacher1));
    }
    
    @Test
    @DirtiesContext
    @WithMockUser(username = "teacher1@ikita.at", authorities = {"TEACHER"})
    public void testTeacherDataToString() {
        teacherCalendarService.saveEntry(testTeacherCalendar1);
        
        String testTeacher1Firstname = testTeacher1.getFirstName();
        String testTeacher1Lastname = testTeacher1.getLastName();
        String completeString = " Pädagoge: " + testTeacher1Firstname + " " + testTeacher1Lastname;
        
        String result = teacherCalendarTeacherService.teacherDataToString(testTeacherCalendar1);
        
        result.equals(completeString);
    }  
}
