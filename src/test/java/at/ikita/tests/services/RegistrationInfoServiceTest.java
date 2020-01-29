package at.ikita.tests.services;

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
import at.ikita.model.Person;

import at.ikita.services.PersonService;
import at.ikita.services.RegistrationInfoService;

/**
 * Tests for RegistrationInfoService
 * 
 * @author Andre.Potocnik@student.uibk.ac.at
 *
 */
@SuppressWarnings("deprecation")
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Main.class)
@WebAppConfiguration
public class RegistrationInfoServiceTest {
    
    @Autowired
    private PersonService personService;
    
    @Autowired
    private RegistrationInfoService registrationInfoService; 
    
    @Test
    @DirtiesContext
    @WithMockUser(username = "teacher1@ikita.at", authorities = {"TEACHER"})
    public void testCreateMailText() {
        Person testPerson = personService.loadEntry(2);
        
        String result = registrationInfoService.createMailText(testPerson);
        
        Assert.assertTrue(result.contains(testPerson.getFirstName()));
        Assert.assertTrue(result.contains(testPerson.getLastName()));
        Assert.assertFalse(result.contains("foo"));
    }
}
