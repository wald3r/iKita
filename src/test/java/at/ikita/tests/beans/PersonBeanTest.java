package at.ikita.tests.beans;



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
import at.ikita.model.Person;
import at.ikita.model.PersonRole;
import at.ikita.model.Picture;
import at.ikita.services.PersonService;
import at.ikita.services.PictureService;
import at.ikita.ui.beans.PersonBean;

/**
 * 
 * junit test class for PersonBean
 * 
 * @author daniel.walder@student.uibk.ac.at
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Main.class)
@WebAppConfiguration
public class PersonBeanTest {



	
	@Autowired
	PersonService personService;
	
	@Autowired
	PictureService pictureService;
	
	@Autowired
	PersonBean personBean;
	
	Person person1;
	Person person2;
	
	
	
	/**
	 * create some test data for following junit tasks
	 */
	@Before
	public void setupTestData(){
		
		
		person1 = personService.loadByEmail("teacher1@ikita.at");
		person2 = Person.create("test234234@ikita.at");
		
		
		String existing_roles[] = {"admin", "teacher", "guardian", "parent"};
		String new_roles[] = {"admin", "teacher", "guardian", "parent"};
		
		Set<PersonRole> roles = new HashSet<>();
	 	roles.add(PersonRole.GUARDIAN);
		
		person2.setFirstName("test");
		person2.setLastName("test");
		person2.setPassword("passwd");
		person2.setRoles(roles);
		
		personService.saveEntry(person2);
		personBean.setPerson(person2);
		
		personBean.setNew_roles(new_roles);
		personBean.setExisting_roles(existing_roles);
		personBean.setEmail("test1@ikita.at");
		personBean.setLastName("Renner");
		personBean.setFirstName("Maximilian");
		personBean.setPrivateTelephone("privateTelephone");
		personBean.setWorkTelephone("workTelephone");
		personBean.setPicture(Picture.create("/test"));
		personBean.setComment("comment");
		personBean.setConfirmedGuardian(true);
		personBean.setMailLunchReminder(true);
		personBean.setMailNotification(true);
		personBean.setRoles(roles);
		
		
		pictureService.saveEntry(personBean.getPicture());
	}
	
	/**
	 * method to test ClearSettings Method
	 * 
	 */
	@Test
	@DirtiesContext
	@WithMockUser(username = "teacher1@ikita.at", authorities = {"TEACHER"})
	public void testClearSettings(){
		  personBean.clearSettings();
		  Assert.assertNull(personBean.getEmail());
		  Assert.assertNull(personBean.getLastName());
		  Assert.assertNull(personBean.getFirstName());
		  Assert.assertNull(personBean.getPrivateTelephone());
		  Assert.assertNull(personBean.getWorkTelephone());
		  Assert.assertNull(personBean.getPicture());
		  Assert.assertNull(personBean.getComment());
		  Assert.assertEquals(false, personBean.isMailLunchReminder());
		  Assert.assertEquals(false, personBean.isMailNotification());
	}
	
	/**
	 * Test do add new Role to a person
	 * 
	 */
	@Test
	@DirtiesContext
	@WithMockUser(username = "teacher1@ikita.at", authorities = {"TEACHER"})
	public void testAddNewRoles(){
		personBean.addNewRoles();
		Assert.assertEquals(true, personBean.getRoles().contains(PersonRole.ADMIN));
		Assert.assertEquals(true, personBean.getRoles().contains(PersonRole.TEACHER));
		Assert.assertEquals(true, personBean.getRoles().contains(PersonRole.PARENT));
		Assert.assertEquals(true, personBean.getRoles().contains(PersonRole.GUARDIAN));
		Assert.assertEquals(true, personBean.isConfirmedGuardian());
	}
	

	/**
	 * test to create a new person
	 * 
	 */
	@Test
	@DirtiesContext
	@WithMockUser(username = "teacher1@ikita.at", authorities = {"TEACHER"})
	public void testNewPerson(){
		personBean.newPerson();
		Assert.assertEquals(0, personBean.getStatus());
		personBean.setEmail("teacher1@ikita.at");
		personBean.newPerson();
		Assert.assertEquals(2, personBean.getStatus());
		
	}

	
	
	/**
	 * test do edit login data 
	 * 
	 */
	@Test
	@DirtiesContext
	@WithMockUser(username = "teacher1@ikita.at", authorities = {"TEACHER"})
	public void testStoreLoginDetails(){
		personBean.getPerson().setEmail("hallo@ikita.at");
		personBean.storeLoginDetails();
		Assert.assertEquals(0, personBean.getStatus());
		personBean.doReloadPerson();
		Assert.assertEquals("hallo@ikita.at", personBean.getPerson().getEmail());		
	}
	
	
	/**
	 * 
	 * test to confirm an unconfirmed guardian
	 * 
	 */
	@Test
	@DirtiesContext
	@WithMockUser(username = "user1@ikita.at", authorities = {"PARENT"})
	public void testConfirmGuardian(){
		Assert.assertEquals(false, personBean.getPerson().isActivated());
		personBean.confirmGuardian();
		Assert.assertEquals(true, personBean.getPerson().isActivated());
		
	}
	
	
	
	/**
	 * test do delete an existing person from database
	 * 
	 */
	@Test
	@DirtiesContext
	@WithMockUser(username = "teacher1@ikita.at", authorities = {"TEACHER"})
	public void testDoDeletePerson(){
		personBean.setPerson(person2);
		personBean.doSavePerson();
		personBean.doDeletePerson();
		Assert.assertNull(personBean.getPerson());
	}
	
	
	/**
	 * test do save an reload person from database
	 * 
	 */
	@Test
	@DirtiesContext
	@WithMockUser(username = "teacher1@ikita.at", authorities = {"TEACHER"})
	public void testSaveandReload(){
		personBean.setPerson(person2);
		personBean.doSavePerson();
		personBean.doReloadPerson();
		Assert.assertEquals(person2, personBean.getPerson());
	}
	
	/**
	 * test to get currently logged in person
	 * 
	 */
	@Test
	@DirtiesContext
	@WithMockUser(username = "teacher1@ikita.at", authorities = {"TEACHER"})
	public void testGetCurrentPerson(){
		person2 = personBean.getCurrentPerson();
		Assert.assertEquals("teacher1@ikita.at", person2.getEmail());
		Assert.assertEquals("Renner", person2.getLastName());
		Assert.assertEquals("Maximilian", person2.getFirstName());
	}
	
    /**
     * 
     * test to add new roles to an existing person
     * 
     */
	@Test
	@DirtiesContext
	@WithMockUser(username = "teacher1@ikita.at", authorities = {"TEACHER"})
	public void testEditExistingRoles(){
		personBean.editExistingRoles();
		Assert.assertEquals(true, personBean.getRoles().contains(PersonRole.ADMIN));
		Assert.assertEquals(true, personBean.getRoles().contains(PersonRole.TEACHER));
		Assert.assertEquals(true, personBean.getRoles().contains(PersonRole.GUARDIAN));
		Assert.assertEquals(true, personBean.getRoles().contains(PersonRole.PARENT));
		Assert.assertEquals(true, personBean.getPerson().isEnabled());
		String existing_roles[] = {"guardian"};
		personBean.setExisting_roles(existing_roles);
		personBean.getRoles().clear();
		personBean.editExistingRoles();
		Assert.assertEquals(false, personBean.getRoles().contains(PersonRole.ADMIN));
		Assert.assertEquals(false, personBean.getRoles().contains(PersonRole.TEACHER));
		Assert.assertEquals(true, personBean.getRoles().contains(PersonRole.GUARDIAN));
		Assert.assertEquals(false, personBean.getPerson().isEnabled());
	}
	
	
	/**
	 * test to edit and store existing person
	 * 
	 */
	@Test
	@DirtiesContext
	@WithMockUser(username = "teacher1@ikita.at", authorities = {"TEACHER"})
	public void testStoreExistingPerson(){
		personBean.storeExistingPerson();
		Assert.assertEquals(0, personBean.getStatus());
		
	}
	

	
	
	
}	
	
	
	








