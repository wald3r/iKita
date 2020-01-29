package at.ikita.tests.beans;

import java.util.Date;
import java.util.List;
import java.util.Calendar;

import javax.faces.model.SelectItem;

import org.junit.runner.RunWith;
import org.primefaces.event.SelectEvent;
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
import at.ikita.model.Child;
import at.ikita.model.ChildCalendar;
import at.ikita.model.Person;
import at.ikita.model.TeacherCalendar;
import at.ikita.services.ChildCalendarService;
import at.ikita.services.ChildService;
import at.ikita.services.PersonService;
import at.ikita.services.TeacherCalendarService;
import at.ikita.services.TeacherCalendarTeacherService;
import at.ikita.ui.beans.TeacherCalendarMonthBean;


/**
 * Tests for TeacherCalendarMonthBean
 * 
 * @author Andre.Potocnik@student.uibk.ac.at
 *
 */
@SuppressWarnings("deprecation")
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Main.class)
@WebAppConfiguration
public class TeacherCalendarMonthBeanTest {
    
    @Autowired
    private TeacherCalendarMonthBean teacherCalendarMonthBean;
    
    @Autowired 
    private ChildCalendarService childCalendarService;
    
    @Autowired
    private TeacherCalendarService teacherCalendarService;
    
    @Autowired
    private PersonService personService;
    
    @Autowired
    private TeacherCalendarTeacherService teacherCalendarTeacherService;
    
    @Autowired
    private ChildService childService;
    
    private TeacherCalendar testTeacherCalendar1;
    
    private TeacherCalendar testTeacherCalendar2;
    
    private Person testTeacher1;
    
    private Person testTeacher2;
    
    private Child testChild1; 
    
    private Child testChild2; 
    
    private ChildCalendar testChildCalendar1;
    
    private ChildCalendar testChildCalendar2; 
    
    private Date testDay;
    
    @Before
    public void setupTestData() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(2016, 4, 8);
        testDay = calendar.getTime();
        
        testTeacher1 = personService.loadEntry(41); // present teacher lunch
        testTeacher2 = personService.loadEntry(42); // present teacher no lunch
        testTeacherCalendar1 = TeacherCalendar.create(testDay, testTeacher1);
        testTeacherCalendar2 = TeacherCalendar.create(testDay, testTeacher2);
        testTeacherCalendar1.setLunch(true);
        testTeacherCalendar2.setLunch(false);
        
        testChild1 = childService.loadEntry(1);
        testChild2 = childService.loadEntry(2);
    }
    
    @Test
    @DirtiesContext
    @WithMockUser(username = "teacher1@ikita.at", authorities = {"TEACHER"})
    public void testGetAmountPresentChildrenByDay() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(testDay);
        calendar.add(Calendar.DAY_OF_MONTH, 3);
        Date testDayWeekLater = calendar.getTime();
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        Date endDay = calendar.getTime();
        
        testChildCalendar1 = ChildCalendar.create(testDay, testChild1);
        testChildCalendar2 = ChildCalendar.create(testDay, testChild2);
        childCalendarService.saveEntry(testChildCalendar1);
        childCalendarService.saveEntry(testChildCalendar2);
        
        testChildCalendar2 = ChildCalendar.create(testDayWeekLater, testChild2);
        childCalendarService.saveEntry(testChildCalendar2);
        
        List<String> amountPresentChildren = teacherCalendarMonthBean.getAmountPresentChildrenByDay(testDay, endDay);
        
        Assert.assertTrue(amountPresentChildren.get(0).equals("2"));
        Assert.assertTrue(amountPresentChildren.get(3).equals("1"));
    }
    
    @Test
    @DirtiesContext
    @WithMockUser(username = "teacher1@ikita.at", authorities = {"TEACHER"})
    public void testGetAmountPresentTeacherByDay() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(testDay);
        calendar.add(Calendar.DAY_OF_MONTH, 3);
        Date testDayWeekLater = calendar.getTime();
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        Date endDay = calendar.getTime();
        
        testTeacherCalendar1 = TeacherCalendar.create(testDay, testTeacher1);
        testTeacherCalendar2 = TeacherCalendar.create(testDay, testTeacher2);
        teacherCalendarService.saveEntry(testTeacherCalendar1);
        teacherCalendarService.saveEntry(testTeacherCalendar2);
        
        testTeacherCalendar2 = TeacherCalendar.create(testDayWeekLater, testTeacher2);
        teacherCalendarService.saveEntry(testTeacherCalendar2);
        
        List<String> amountPresentTeacher = teacherCalendarMonthBean.getAmountPresentTeacherByDay(testDay, endDay);
        
        Assert.assertTrue(amountPresentTeacher.get(0).equals("2"));
        Assert.assertTrue(amountPresentTeacher.get(3).equals("1"));
    }
    
    @Test
    @DirtiesContext
    @WithMockUser(username = "teacher1@ikita.at", authorities = {"TEACHER"})
    public void testGetAmountBookedMealsByDay() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(testDay);
        calendar.add(Calendar.DAY_OF_MONTH, 3);
        Date testDayWeekLater = calendar.getTime();
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        Date endDay = calendar.getTime();
        
        testTeacherCalendar1 = TeacherCalendar.create(testDay, testTeacher1);
        testTeacherCalendar2 = TeacherCalendar.create(testDay, testTeacher2);
        testTeacherCalendar1.setLunch(true);
        teacherCalendarService.saveEntry(testTeacherCalendar1);
        teacherCalendarService.saveEntry(testTeacherCalendar2);
        
        testTeacherCalendar2 = TeacherCalendar.create(testDayWeekLater, testTeacher2);
        teacherCalendarService.saveEntry(testTeacherCalendar2);
        
        testChildCalendar1 = ChildCalendar.create(testDay, testChild1);
        testChildCalendar2 = ChildCalendar.create(testDay, testChild2);
        testChildCalendar1.setLunch(true);
        childCalendarService.saveEntry(testChildCalendar1);
        childCalendarService.saveEntry(testChildCalendar2);
        
        testChildCalendar2 = ChildCalendar.create(testDayWeekLater, testChild2);
        testChildCalendar2.setLunch(true);
        childCalendarService.saveEntry(testChildCalendar2);
        
        List<String> bookedMeals = teacherCalendarMonthBean.getAmountBookedMealsByDay(testDay, endDay);
        
        Assert.assertTrue(bookedMeals.get(0).equals("2"));
        Assert.assertTrue(bookedMeals.get(3).equals("1"));
    }
    
    @Test
    @DirtiesContext
    @WithMockUser(username = "teacher1@ikita.at", authorities = {"TEACHER"})
    public void testInitSelectOneListbox() {
        teacherCalendarMonthBean.initSelectOneListbox(testDay);
        
        List<SelectItem> testSelectableTeacher = teacherCalendarMonthBean.getTeacherItems();
        
        Assert.assertEquals(2, testSelectableTeacher.size());
    }
    
    @Test
    @DirtiesContext
    @WithMockUser(username = "teacher1@ikita.at", authorities = {"TEACHER"})
    public void testSaveTeacherCalendarEntry() {
        DefaultScheduleEvent event = new DefaultScheduleEvent(teacherCalendarTeacherService.teacherDataToString(testTeacherCalendar1), testDay, testDay, true);
        teacherCalendarMonthBean.initSelectOneListbox(testDay);
        teacherCalendarMonthBean.setEvent(event);
        teacherCalendarMonthBean.saveTeacherCalendarEntry();
        
        Assert.assertNull(teacherCalendarMonthBean.getSelectedTeacherEmail());
        
        List<TeacherCalendar> newTeacherCalendar = teacherCalendarService.loadByDate(testDay);
        Assert.assertEquals(0, newTeacherCalendar.size());
        
        teacherCalendarMonthBean.setSelectedTeacherEmail(testTeacher1.getEmail());
        teacherCalendarMonthBean.saveTeacherCalendarEntry();
        
        newTeacherCalendar = teacherCalendarService.loadByDate(testDay);
        Assert.assertEquals(1, newTeacherCalendar.size());     
    }
}
