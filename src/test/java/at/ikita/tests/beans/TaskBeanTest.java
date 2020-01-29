package at.ikita.tests.beans;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;


import at.ikita.Main;
import at.ikita.model.Gender;
import at.ikita.model.ParentTask;
import at.ikita.model.Person;
import at.ikita.model.PersonRole;
import at.ikita.services.ParentTaskService;
import at.ikita.services.PersonService;
import at.ikita.ui.beans.TaskBean;

/*
/**
 * 
 * junit test class for TaskBean
 * 
 * @author daniel.walder@student.uibk.ac.at
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Main.class)
@WebAppConfiguration
public class TaskBeanTest {

	
	@Autowired
	ParentTaskService parentTaskService;
	
	@Autowired
	PersonService personService;

	@Autowired
	TaskBean taskBean;
	
    private ParentTask task1 = ParentTask.create();

    
    Person person;
	
    /**
     * Create some test data for junit tests
     * 
     */
    @Before
    public void setupTestData() {
    
    	
     	person = personService.loadByEmail("teacher1@ikita.at");
     	
    	Date startDate = new Date();
    	Date endDate = new Date();
    	String description = "i am a test";
    	String title = "testtitle";
    	boolean urgent = false;
    	
    	
    	task1.setDescription(description);
    	task1.setEndDate(endDate);
    	task1.setStartDate(startDate);
    	task1.setTitle(title);
    	task1.setUrgent(urgent);
    	task1.setPerson(person);
    
    	parentTaskService.saveEntry(task1);
    	
    	taskBean.setPerson("<teacher1@ikita.at>");
    	taskBean.setDescription(description);
    	taskBean.setTitle(title);
    	taskBean.setUrgent(urgent);
    	taskBean.setStartDate(startDate);
    	taskBean.setEndDate(endDate);
    	
 
    	
    }
    
    /**
     * Test method ClearSettings
     * 
     */
    @Test
    @DirtiesContext
    @WithMockUser(username = "teacher1@ikita.at", authorities = {"TEACHER"})
    public void testClearSettings(){

    	taskBean.clearSettings();
    	Assert.assertNull(taskBean.getPerson1());
    	Assert.assertNull(taskBean.getDescription());
    	Assert.assertNull(taskBean.getStartDate());
    	Assert.assertNull(taskBean.getEndDate());
    	Assert.assertNull(taskBean.getTitle());
    	Assert.assertFalse(taskBean.isUrgent());
    	Assert.assertNull(taskBean.getPerson());

    }
    
    
    /**
     * Method to test the creation of new tasks
     * 
     */
    @Test
    @DirtiesContext
    @WithMockUser(username = "teacher1@ikita.at", authorities = {"TEACHER"})
    public void testNewTask(){
    	taskBean.newTask();
    	Assert.assertEquals(0, taskBean.getStatus());
    	taskBean.setEndDate(new Date(40000));    
    	taskBean.setStartDate(new Date());
    	taskBean.newTask();
    	Assert.assertEquals(2, taskBean.getStatus());
    	taskBean.setStartDate(new Date());  
    	taskBean.setEndDate(new Date());
    	taskBean.newTask();
    	Assert.assertEquals(0, taskBean.getStatus());
    }
    
    
    /**
     * Test do Save and Reload Tasks from database
     * 
     */
    @Test
    @DirtiesContext
    @WithMockUser(username = "teacher1@ikita.at", authorities = {"TEACHER"})
    public void testSaveAndReloadTask(){
    	taskBean.setTask(task1);
    	taskBean.doSaveTask();
    	taskBean.doReloadTask();
    	Assert.assertEquals(task1, taskBean.getTask());

    }
    
    
    /**
     * Test do add Person to a task
     * 
     */
    @Test
    @DirtiesContext
    @WithMockUser(username = "teacher1@ikita.at", authorities = {"TEACHER"})
    public void testAddPerson(){
    	taskBean.newTask();
    	Assert.assertEquals(person, taskBean.getPerson1());
    }
    
    
    
   /**
    * test to reopen a task
    * 
    */
    @Test
    @DirtiesContext
    @WithMockUser(username = "teacher1@ikita.at", authorities = {"TEACHER"})
    public void testOpenTask(){
    	taskBean.newTask();
    	taskBean.getTask().setDone(true);
    	Assert.assertEquals(true, taskBean.getTask().isDone());
    	taskBean.openTask();
    	Assert.assertNull(taskBean.getTask());
    }
    
    
    /**
     * test to close a task
     * 
     */
    @Test
    @DirtiesContext
    @WithMockUser(username = "teacher1@ikita.at", authorities = {"TEACHER"})
    public void testFinishTask(){
    	taskBean.newTask();
    	Assert.assertEquals(false, taskBean.getTask().isDone());
    	taskBean.finishTask();
    	Assert.assertNull(taskBean.getTask());

    }
    
    
    
    /**
     * test do delete existing tasks from database
     * 
     */
    @Test
    @DirtiesContext
    @WithMockUser(username = "teacher1@ikita.at", authorities = {"TEACHER"})
    public void testdoDeleteTask(){
    	taskBean.newTask();
    	taskBean.doDeleteTask();
    	Assert.assertNull(taskBean.getTask());
    }
    
    /**
     * test to edit existing tasks 
     * 
     */
    @Test
    @DirtiesContext
    @WithMockUser(username = "teacher1@ikita.at", authorities = {"TEACHER"})
    public void testEditTask(){
    	taskBean.newTask();
    	taskBean.editTask();
    	Assert.assertEquals(0, taskBean.getStatus());
    	taskBean.getTask().setEndDate(new Date(40000));
    	taskBean.getTask().setStartDate(new Date());
    	taskBean.editTask();
    	Assert.assertEquals(2, taskBean.getStatus());
    	taskBean.getTask().setEndDate(new Date());
    	taskBean.getTask().setStartDate(new Date());
    	taskBean.editTask();
    	Assert.assertEquals(0, taskBean.getStatus());
    }
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    

	
}
