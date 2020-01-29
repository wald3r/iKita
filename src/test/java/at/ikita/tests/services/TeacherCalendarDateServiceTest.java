package at.ikita.tests.services;

import java.util.Date;
import java.util.Calendar;

import org.junit.runner.RunWith;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import at.ikita.Main;
import at.ikita.services.TeacherCalendarDateService;


/**
 * Tests for TeacherCalendarDateService
 * 
 * @author Andre.Potocnik@student.uibk.ac.at
 *
 */
@SuppressWarnings("deprecation")
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Main.class)
@WebAppConfiguration
public class TeacherCalendarDateServiceTest {
    
    @Autowired
    private TeacherCalendarDateService teacherCalendarDateService;
    
    @Test
    @DirtiesContext
    @WithMockUser(username = "teacher1@ikita.at", authorities = {"TEACHER"})
    public void testAddDay() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(2016, 4, 8);
        
        Date currentDay = calendar.getTime(); // test start day
        
        calendar.set(2016, 4, 9); 
        
        Date expectedDay = calendar.getTime(); // test next day
        
        calendar = teacherCalendarDateService.addDay(currentDay);
        
        Date result = calendar.getTime();
        
        Assert.assertTrue(result.equals(expectedDay));
    }
    
    @Test
    @DirtiesContext
    @WithMockUser(username = "teacher1@ikita.at", authorities = {"TEACHER"})
    public void testTimeToDate() {
        Calendar calendar = Calendar.getInstance();
        
        /* Time which is passed as argument to timeToDate() */
        calendar.set(Calendar.HOUR_OF_DAY, 14);
        calendar.set(Calendar.MINUTE, 30);
        calendar.set(Calendar.SECOND, 00);
        Date time = calendar.getTime();
        
        /* Date which is passed as argument to timeToDate() */
        calendar = Calendar.getInstance();
        calendar.set(2016, 4, 18);
        Date day = calendar.getTime();
        
        /* Result from timeToDate() */
        Date result = teacherCalendarDateService.timeToDate(time, day);
        
        /* Expected time and date */
        calendar.set(2016, 4, 18);
        calendar.set(Calendar.HOUR_OF_DAY, 14);
        calendar.set(Calendar.MINUTE, 30);
        calendar.set(Calendar.SECOND, 00);
        Date expected = calendar.getTime();
        
        Assert.assertTrue(result.equals(expected));
    }
}
