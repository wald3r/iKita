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
import at.ikita.model.ParentTask;
import at.ikita.services.ParentTaskService;

import at.ikita.services.TaskNotificationService;

/**
 * Tests for TaskNotificationService
 * 
 * @author Andre.Potocnik@student.uibk.ac.at
 *
 */
@SuppressWarnings("deprecation")
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Main.class)
@WebAppConfiguration
public class TaskNotificationServiceTest {

    @Autowired
    private ParentTaskService ParentTaskService;
    
    @Autowired
    private TaskNotificationService taskNotificationService;
    
    @Test
    @DirtiesContext
    @WithMockUser(username = "teacher1@ikita.at", authorities = {"TEACHER"})
    public void testCreateMailText() {
        ParentTask testTask = ParentTaskService.loadEntry(2);
        
        String result = taskNotificationService.createMailText(testTask);
        Assert.assertTrue(result.contains(testTask.getPerson().getFirstName()));
        Assert.assertTrue(result.contains(testTask.getPerson().getLastName()));
        Assert.assertTrue(result.contains(testTask.getTitle()));
        Assert.assertFalse(result.contains("DRINGEND"));
        Assert.assertTrue(result.contains(testTask.getEndDate().toString()));
        Assert.assertFalse(result.contains("Beschreibung"));
        
        testTask.setDescription("foo");
        result = taskNotificationService.createMailText(testTask);
        Assert.assertTrue(result.contains("Beschreibung"));
    }
}
