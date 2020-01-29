package at.ikita.tests.services;

import java.util.Date;
import java.util.Calendar;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.Assert;
import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import at.ikita.Main;
import at.ikita.model.Child;
import at.ikita.model.ChildCalendar;
import at.ikita.services.ChildService;
import at.ikita.services.TeacherCalendarChildService;


/**
 * Tests for TeacherCalendarChildService
 * 
 * @author Andre.Potocnik@student.uibk.ac.at
 *
 */
@SuppressWarnings("deprecation")
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Main.class)
@WebAppConfiguration
public class TeacherCalendarChildServiceTest {
    
    @Autowired
    private ChildService childService;
           
    @Autowired
    private TeacherCalendarChildService teacherCalendarChildService;
    
    private Child testChild1; 
    
    private Child testChild2;
    
    private ChildCalendar testChildCalendar1; 
    
    private ChildCalendar testChildCalendar2;
    
    private Date testDay;
    
    private Calendar calendar;
    
    @Before
    public void setupTestData() {
        calendar = Calendar.getInstance();
        calendar.set(2016, 4, 8);
        
        testDay = calendar.getTime();
        
        testChild1 = childService.loadEntry(1);
        testChild2 = childService.loadEntry(2);
        
        testChildCalendar1 = ChildCalendar.create(testDay, testChild1); // Default bring / pickup time
        testChildCalendar2 = ChildCalendar.create(testDay, testChild2); // Alternative bring / pickup time
    }
    
    @Test
    @DirtiesContext
    @WithMockUser(username = "teacher1@ikita.at", authorities = {"TEACHER"})
    public void testGetActualBringPickupTime() {
             
        Date testChild1DefaultBringTime = testChild1.getDefaultBringTime();
        Date testChild1DefaultPickupTime = testChild1.getDefaultPickupTime();
        
        Date testChild2AlternativeBringTime = testChild1.getDefaultBringTime(); 
        Date testChild2AlternativePickupTime = testChild1.getDefaultPickupTime();
        
        
        /* Set alternative bring / pickup time for second child */
        calendar.setTime(testChild2AlternativeBringTime);
        calendar.add(Calendar.HOUR, 1);
        testChild2AlternativeBringTime = calendar.getTime();
        testChildCalendar2.setBringTime(testChild2AlternativeBringTime);
        
        calendar.setTime(testChild2AlternativePickupTime);
        calendar.add(Calendar.HOUR, 1);
        testChild2AlternativePickupTime = calendar.getTime();
        testChildCalendar2.setPickupTime(testChild2AlternativePickupTime);
        
        Assert.assertNull(testChildCalendar1.getBringTime());
        Assert.assertNull(testChildCalendar1.getPickupTime());
        Assert.assertEquals(teacherCalendarChildService.getActualBringPickupTime(testChildCalendar1, 1), testChild1DefaultBringTime);
        Assert.assertEquals(teacherCalendarChildService.getActualBringPickupTime(testChildCalendar1, 2), testChild1DefaultPickupTime);
        
        Assert.assertNotNull(testChildCalendar2.getBringTime());
        Assert.assertNotNull(testChildCalendar2.getPickupTime());
        Assert.assertEquals(teacherCalendarChildService.getActualBringPickupTime(testChildCalendar2, 1), testChild2AlternativeBringTime);
        Assert.assertEquals(teacherCalendarChildService.getActualBringPickupTime(testChildCalendar2, 2), testChild2AlternativePickupTime);
    }
    
    @Test
    @DirtiesContext
    @WithMockUser(username = "teacher1@ikita.at", authorities = {"TEACHER"})
    public void testChildDataToString() {
        String result = teacherCalendarChildService.childDataToString(testChildCalendar1);
        
        Assert.assertTrue(result.contains(testChildCalendar1.getChild().getFirstName()));
        Assert.assertTrue(result.contains(testChildCalendar1.getChild().getLastName()));
    }
}
