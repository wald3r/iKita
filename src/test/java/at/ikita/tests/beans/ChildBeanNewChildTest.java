package at.ikita.tests.beans;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.primefaces.event.FileUploadEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import at.ikita.Main;
import at.ikita.model.Child;
import at.ikita.model.DayOfWeek;
import at.ikita.model.Gender;
import at.ikita.model.Person;
import at.ikita.model.Picture;
import at.ikita.model.PersonRole;
import at.ikita.services.ChildService;
import at.ikita.services.PersonService;
import at.ikita.ui.beans.ChildBean;

@SuppressWarnings("deprecation")
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Main.class)
@WebAppConfiguration

/**
 * 
 * @author kerstin.klabischnig@student.uibk.ac.at
 *
 */
public class ChildBeanNewChildTest {
	
	@Autowired 
	ChildService childService;
	@Autowired
	PersonService personService;
	@Autowired
	ChildBean childBean;

	
	
	@Before
	public void setupTestData(){
			}
	
	/**
	 * try to create a new child without some required data
	 */
	
	@Test
	@DirtiesContext
	@WithMockUser(username = "teacher1@ikita.at", authorities = {"TEACHER"})
	public void testCreateChildFail(){
		childBean.setLastName("test");
		childBean.setFirstName("child");
		Date birthday = new Date(2000, 10, 10);
		childBean.setBirthday(birthday);
		String[] defaultPresences1 = new String[1];
		defaultPresences1[0] = "mon";
		childBean.setDefaultPresences1(defaultPresences1);
		childBean.setSex("male");
		childBean.setEmergencyContact1("<emergency@ikita.at>");

		childBean.newChild();
		Assert.assertEquals(1, childBean.getStatus());
	}
	
	/**
	 * try to create a new child if child already exists
	 */
	@Test
	@DirtiesContext
	@WithMockUser(username = "teacher1@ikita.at", authorities = {"TEACHER"})
	public void testCreateChildFail1(){
		
		Date birthdaytest = new GregorianCalendar(2000, 11, 23).getTime();
		Child testchild = Child.create("test", "child", birthdaytest);
		testchild.setRegistrationDate(new GregorianCalendar(2017, 3, 1).getTime());
		testchild.setDeRegistrationDate(new GregorianCalendar(2017, 4, 1).getTime()); 
		childService.saveEntry(testchild);
		
		Set<PersonRole> roles = new HashSet<>();
		roles.add(PersonRole.PARENT);
		Person parent = Person.create("parent@ikita.at", "parent", "tester", "passwd", roles);
		personService.saveEntry(parent);
		
		childBean.setLastName("test");
		childBean.setFirstName("child");
		childBean.setBirthday(birthdaytest);
		String[] defaultPresences1 = new String[1];
		defaultPresences1[0] = "mon";
		childBean.setDefaultPresences1(defaultPresences1);
		childBean.setSex("male");
		childBean.setEmergencyContact1("<tester@ikita.at>");
		Date registration = new Date(2001, 10, 10);
		childBean.setRegistrationDate(registration);
		Date deregistration = new Date(2002, 10, 10);
		childBean.setDeRegistrationDate(deregistration);
		Set <String> parent1 = new HashSet<>();
		parent1.add("<parent@ikita.at>");
		childBean.setSelectedParents(parent1);
		childBean.newChild();
		Assert.assertEquals(4, childBean.getStatus());
	}
	
	/**
	 * finally test for creating a new child, save it and delete it
	 */
	@Test
	@DirtiesContext
	@WithMockUser(username = "teacher1@ikita.at", authorities = {"TEACHER"})
	public void testCreateSaveDeleteChild(){
		//create Parent
		Set<PersonRole> roles = new HashSet<>();
		roles.add(PersonRole.PARENT);
		Person parent = Person.create("parent@ikita.at", "parent", "tester", "passwd", roles);
		personService.saveEntry(parent);
		
		childBean.setLastName("test");
		childBean.setFirstName("child");
		Date birthday = new Date(2000, 10, 10);
		childBean.setBirthday(birthday);
		String[] defaultPresences1 = new String[1];
		defaultPresences1[0] = "mon";
		childBean.setDefaultPresences1(defaultPresences1);
		childBean.setSex("male");
		childBean.setEmergencyContact1("<tester@ikita.at>");
		Date registration = new Date(2001, 10, 10);
		childBean.setRegistrationDate(registration);
		Date deregistration = new Date(2002, 10, 10);
		childBean.setDeRegistrationDate(deregistration);
		Set <String> parent1 = new HashSet<>();
		parent1.add("<parent@ikita.at>");
		childBean.setSelectedParents(parent1);
		childBean.newChild();
		Assert.assertEquals(0, childBean.getStatus());
		childBean.doReloadChild();
		Assert.assertEquals(childBean.getLastName(), "test");
		Assert.assertEquals(childBean.getFirstName(), "child");
		Assert.assertEquals(childBean.getBirthday(), birthday);
		childBean.doDeleteChild();
		Assert.assertNull(childBean.getChild());
		
	}	
	
	@Test
	@DirtiesContext
	@WithMockUser(username = "teacher1@ikita.at", authorities = {"TEACHER"})
	public void testEditExistingChild(){
		Set<PersonRole> roles = new HashSet<>();
		roles.add(PersonRole.PARENT);
		Person parent = Person.create("parent@ikita.at", "parent", "tester", "passwd", roles);
		personService.saveEntry(parent);
		
		childBean.setLastName("test");
		childBean.setFirstName("child");
		Date birthday = new Date(2000, 10, 10);
		childBean.setBirthday(birthday);
		String[] defaultPresences1 = new String[1];
		defaultPresences1[0] = "mon";
		childBean.setDefaultPresences1(defaultPresences1);
		childBean.setSex("male");
		childBean.setEmergencyContact1("<tester@ikita.at>");
		Date registration = new Date(2001, 10, 10);
		childBean.setRegistrationDate(registration);
		Date deregistration = new Date(2002, 10, 10);
		childBean.setDeRegistrationDate(deregistration);
		Set <String> parent1 = new HashSet<>();
		parent1.add("<parent@ikita.at>");
		childBean.setSelectedParents(parent1);
		childBean.newChild();
		Assert.assertEquals(0, childBean.getStatus());
		
		childBean.setSex("female");
		childBean.setEnum_gender(Gender.FEMALE);
		
		Assert.assertEquals(Gender.FEMALE, childBean.getEnum_gender());
		childBean.editExistingChild();
		
		Assert.assertEquals(0, childBean.getStatus());

		Assert.assertEquals(Gender.FEMALE, childBean.getEnum_gender());
	}
}