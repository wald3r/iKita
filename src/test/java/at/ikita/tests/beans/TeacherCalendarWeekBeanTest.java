package at.ikita.tests.beans;

import java.util.Date;
import java.util.List;
import java.util.Calendar;

import javax.faces.model.SelectItem;

import org.junit.runner.RunWith;
import org.primefaces.model.DefaultScheduleEvent;
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
import at.ikita.ui.beans.TeacherCalendarDayWeekBean;


/**
 * Tests for TeacherCalendarDayWeekBean
 * 
 * @author Andre.Potocnik@student.uibk.ac.at
 *
 */
@SuppressWarnings("deprecation")
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Main.class)
@WebAppConfiguration
public class TeacherCalendarWeekBeanTest {

    @Autowired
    private TeacherCalendarDayWeekBean teacherCalendarDayWeekBean;
    
    @Autowired 
    private TeacherCalendarTeacherService teacherCalendarTeacherService;
    
    @Autowired
    private TeacherCalendarService teacherCalendarService;
    
    @Autowired
    private PersonService personService;
    
    private TeacherCalendar testTeacherCalendar1;
    
    private Person testTeacher1;
    
    private Date testDay;
    
    @Before
    public void setupTestData() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(2016, 4, 8);
        testDay = calendar.getTime();
        
        testTeacher1 = personService.loadEntry(41); // present teacher
        
        testTeacherCalendar1 = TeacherCalendar.create(testDay, testTeacher1);
    }
    
    @Test
    @DirtiesContext
    @WithMockUser(username = "teacher1@ikita.at", authorities = {"TEACHER"})
    public void testInitSelectOneListbox() {
        teacherCalendarDayWeekBean.initSelectOneListbox(testDay);
        
        List<SelectItem> testSelectableTeacher = teacherCalendarDayWeekBean.getTeacherItems();
        
        Assert.assertEquals(2, testSelectableTeacher.size());
    }
    
    @Test
    @DirtiesContext
    @WithMockUser(username = "teacher1@ikita.at", authorities = {"TEACHER"})
    public void testSaveTeacherCalendarEntry() {
        DefaultScheduleEvent event = new DefaultScheduleEvent(teacherCalendarTeacherService.teacherDataToString(testTeacherCalendar1), testDay, testDay, true);
        teacherCalendarDayWeekBean.initSelectOneListbox(testDay);
        teacherCalendarDayWeekBean.setEvent(event);
        teacherCalendarDayWeekBean.saveTeacherCalendarEntry();
        
        Assert.assertNull(teacherCalendarDayWeekBean.getSelectedTeacherEmail());
        
        List<TeacherCalendar> newTeacherCalendar = teacherCalendarService.loadByDate(testDay);
        Assert.assertEquals(0, newTeacherCalendar.size());
        
        teacherCalendarDayWeekBean.setSelectedTeacherEmail(testTeacher1.getEmail());
        teacherCalendarDayWeekBean.saveTeacherCalendarEntry();
        
        newTeacherCalendar = teacherCalendarService.loadByDate(testDay);
        Assert.assertEquals(1, newTeacherCalendar.size());     
    }
}
