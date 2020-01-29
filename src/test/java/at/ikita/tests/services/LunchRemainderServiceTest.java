package at.ikita.tests.services;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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
import at.ikita.model.PersonRole;

import at.ikita.services.LunchRemainderService;
import at.ikita.services.PersonService;

/**
 * Tests for LunchReminderService
 * 
 * @author Andre.Potocnik@student.uibk.ac.at
 *
 */
@SuppressWarnings("deprecation")
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Main.class)
@WebAppConfiguration
public class LunchRemainderServiceTest {
    
    @Autowired
    private PersonService personService;
    
    @Autowired
    private LunchRemainderService lunchRemainderService;
    
    private Person testNotLunchPerson; 
    
    private Date testDay;
    
    @Before
    public void setupTestData() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(2016, 4, 8);
        testDay = calendar.getTime();
        
        Set<PersonRole> personRoleSet = new HashSet<>();
        personRoleSet.add(PersonRole.PARENT);
        testNotLunchPerson = Person.create("noLunch@meal.com", "Lunch", "No", "noLunch123", personRoleSet);
    }
    
    
    @Test
    @DirtiesContext
    @WithMockUser(username = "teacher1@ikita.at", authorities = {"TEACHER"})
    public void testGetReminderCandidates() {
        personService.saveEntry(testNotLunchPerson);
        
        List<Person> allParents = personService.loadAllByRole(PersonRole.PARENT);
        int lunchRemainderCounter = 0;
        
        for(Person i : allParents) {
            if(i.isMailLunchReminder()) {lunchRemainderCounter++;}
        }
        
        List<Person> testReminderCandidates = lunchRemainderService.getReminderCandidates();
        
        Assert.assertEquals(testReminderCandidates.size(), lunchRemainderCounter);
        Assert.assertFalse(testReminderCandidates.contains(testNotLunchPerson));
    }
    
    @Test
    @DirtiesContext
    @WithMockUser(username = "teacher1@ikita.at", authorities = {"TEACHER"})
    public void testMailHeader() {
        String result = lunchRemainderService.mailHeader(testNotLunchPerson);
        
        Assert.assertTrue(result.contains(testNotLunchPerson.getFirstName()));
        Assert.assertTrue(result.contains(testNotLunchPerson.getLastName()));
        Assert.assertFalse(result.contains("foo"));
    }
    
    @Test
    @DirtiesContext
    @WithMockUser(username = "teacher1@ikita.at", authorities = {"TEACHER"})
    public void testGetStartDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(testDay);
        calendar.add(Calendar.DAY_OF_MONTH, 3);
        Date expected = calendar.getTime();
        Date result = lunchRemainderService.getStartDate(testDay); // result should be two days after testDay param
        
        Assert.assertTrue(expected.equals(result));   
    }
    
    @Test
    @DirtiesContext
    @WithMockUser(username = "teacher1@ikita.at", authorities = {"TEACHER"})
    public void testGetEndDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(testDay);
        calendar.add(Calendar.DAY_OF_MONTH, 5);
        Date expected = calendar.getTime();
        Date result = lunchRemainderService.getEndDate(testDay);
        
        Assert.assertTrue(expected.equals(result));
    }
    
    @Test
    @DirtiesContext
    @WithMockUser(username = "teacher1@ikita.at", authorities = {"TEACHER"})
    public void testAddDay() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(testDay);
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        
        Date expected = calendar.getTime(); // test next day
        Date result = lunchRemainderService.addDay(testDay);
        
        Assert.assertTrue(result.equals(expected));
    } 
}