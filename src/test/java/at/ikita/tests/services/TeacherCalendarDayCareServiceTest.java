package at.ikita.tests.services;

import java.util.Date;
import java.sql.Time;
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
import at.ikita.model.DayCareCalendar;
import at.ikita.services.DayCareCalendarService;
import at.ikita.services.TeacherCalendarDayCareService;


/**
 * Tests for TeacherCalendarDayCareService
 * 
 * @author Andre.Potocnik@student.uibk.ac.at
 *
 */
@SuppressWarnings("deprecation")
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Main.class)
@WebAppConfiguration
public class TeacherCalendarDayCareServiceTest {

    @Autowired
    private TeacherCalendarDayCareService teacherCalendarDayCareService;
    
    @Autowired
    private DayCareCalendarService dayCareCalendarService;
    
    private Calendar calendar;
    
    private Date testDay;
    
    private DayCareCalendar dayCareCalendar;
    
    @Before
    public void setupTestData() {
        calendar = Calendar.getInstance();
        calendar.set(2016, 4, 8);
        testDay = calendar.getTime();
    }
    
    @Test
    @DirtiesContext
    @WithMockUser(username = "teacher1@ikita.at", authorities = {"TEACHER"})
    public void testGetMaxAttendancePerDay() {
        String result = teacherCalendarDayCareService.getMaxAttendancePerDay(testDay);
        
        /* Test if no dayCareCalendar entry exists */
        Assert.assertTrue(result.equals("-"));
        
        /* Test if maxAttendance is not set */
        dayCareCalendar = DayCareCalendar.create(testDay);
        dayCareCalendar.setOpeningTime(Time.valueOf("00:00:00"));
        dayCareCalendar.setClosingTime(Time.valueOf("00:00:00"));
        dayCareCalendar.setMealPrice(0.0);
        dayCareCalendarService.saveEntry(dayCareCalendar);
        result = teacherCalendarDayCareService.getMaxAttendancePerDay(testDay);
        Assert.assertTrue(result.equals("-"));
        
        /* Test if maxAttendance is set */
        calendar.set(2016, 4, 9);
        testDay = calendar.getTime();
        dayCareCalendar = DayCareCalendar.create(testDay);
        dayCareCalendar.setOpeningTime(Time.valueOf("00:00:00"));
        dayCareCalendar.setClosingTime(Time.valueOf("00:00:00"));
        dayCareCalendar.setMealPrice(0.0);
        dayCareCalendar.setMaxAttendance(99);
        dayCareCalendarService.saveEntry(dayCareCalendar);
        result = teacherCalendarDayCareService.getMaxAttendancePerDay(testDay);
        Assert.assertTrue(result.equals("99"));
    }
    
    @Test
    @DirtiesContext
    @WithMockUser(username = "teacher1@ikita.at", authorities = {"TEACHER"})
    public void testInitDayCareCalendar() {
        dayCareCalendar = teacherCalendarDayCareService.initDayCareCalendar(testDay);
        Double mealPrice = new Double(0.0);
        
        Assert.assertEquals(0, dayCareCalendar.getMaxAttendance());
        Assert.assertTrue(dayCareCalendar.getMeal1Description().equals(""));
        Assert.assertTrue(dayCareCalendar.getMeal2Description().equals(""));
        Assert.assertTrue(dayCareCalendar.getMealPrice().equals(mealPrice));
    }
    
    @Test
    @DirtiesContext
    @WithMockUser(username = "teacher1@ikita.at", authorities = {"TEACHER"})
    public void testSetDayCareCalendarClosed() {
        boolean dayCareClosed = false;
        Double mealPrice = new Double(0.0);
        dayCareCalendar = DayCareCalendar.create(testDay);
        
        dayCareCalendar = teacherCalendarDayCareService.setDayCareCalendarClosed(dayCareCalendar, dayCareClosed);
        Assert.assertNull(dayCareCalendar.getOpeningTime());
        Assert.assertNull(dayCareCalendar.getClosingTime());
        
        dayCareClosed = true;
        dayCareCalendar = teacherCalendarDayCareService.setDayCareCalendarClosed(dayCareCalendar, dayCareClosed);
        Assert.assertTrue(dayCareCalendar.getOpeningTime().equals(Time.valueOf("00:00:00")));
        Assert.assertTrue(dayCareCalendar.getClosingTime().equals(Time.valueOf("00:00:00")));
        Assert.assertEquals(0, dayCareCalendar.getMaxAttendance());
        Assert.assertTrue(dayCareCalendar.getMeal1Description().equals(""));
        Assert.assertTrue(dayCareCalendar.getMeal2Description().equals(""));
        Assert.assertTrue(dayCareCalendar.getMealPrice().equals(mealPrice));
    }
}
