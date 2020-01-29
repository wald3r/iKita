package at.ikita.tests.beans;

import java.util.Date;
import java.util.GregorianCalendar;
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
import at.ikita.model.Child;
import at.ikita.model.DayOfWeek;
import at.ikita.model.Gender;
import at.ikita.model.Person;
import at.ikita.model.PersonRole;
import at.ikita.services.ChildService;
import at.ikita.services.PersonService;
import at.ikita.ui.beans.ChildBean;

/**
 * 
 * @author kerstin.klabischnig@student.uibk.ac.at
 *
 */
@SuppressWarnings("deprecation")
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Main.class)
@WebAppConfiguration
public class ChildBeanTest {
	
	@Autowired 
	ChildService childService;
	
	@Autowired
	PersonService personService;
	
	@Autowired
	ChildBean childBean;
	
	
	
	Child child;
	Person parent;
	
	Set <PersonRole> roles = new HashSet<>();
	Set<Child> children = new HashSet<>();
	Set<String> selectedParents = new HashSet<>();
	Set<Person> parents = new HashSet<>();
	String[] defaultPresences1 = new String[7];	
	Set <DayOfWeek> days = new HashSet<>();
	
	/**
	 * Creating Person and Child and setup Data for test
	 */
	@Before
	public void setupTestData(){
		Date birthday = new Date(2000, 10, 10);
		Date registration = new Date(2001, 10, 10);
		Date deregistration = new Date(2002, 10, 10);
		child = Child.create("test", "child", birthday);
		
		//create Parent for Child
    	parent = Person.create("<test@ikita.at>");
		parent.setFirstName("test");
		parent.setLastName("parent");
		parent.setPassword("passwd");
		parent.setRoles(roles);
		
		//parent is now parent of child
		children.add(child);
		parent.setChildren(children);
	
		//child has now parent
		parents.add(parent);
		child.setParents(parents);
		
		//create Sibling for Child
		Child sibling = Child.create("test", "sibling", new Date());
		parent.addChild(sibling);
		
		child.setDeRegistrationDate(deregistration);
		child.setRegistrationDate(registration);
		
		childBean.setChild(child);
		defaultPresences1[0] = "mon";
		defaultPresences1[1] = "tue";
		defaultPresences1[2] = "wed";
		defaultPresences1[3] = "thurs";
		defaultPresences1[4] = "fri";
		defaultPresences1[5] = "sat";
		defaultPresences1[6] = "sun";
		childBean.setDefaultPresences1(defaultPresences1);
		
		days.add(DayOfWeek.MONDAY);
		days.add(DayOfWeek.TUESDAY);
		days.add(DayOfWeek.WEDNESDAY);
		days.add(DayOfWeek.THURSDAY);
		days.add(DayOfWeek.FRIDAY);
		days.add(DayOfWeek.SATURDAY);
		days.add(DayOfWeek.SUNDAY);
		child.setDefaultPresences(days);
		
		childBean.setLastName("test");
		childBean.setFirstName("child");
		childBean.setBirthday(birthday);
	    childBean.setEnum_gender(Gender.MALE);
	    childBean.setDeRegistrationDate(deregistration);
	    childBean.setRegistrationDate(registration);
	    childBean.setComment("comment");
	    childBean.setEmergencyContact1("<emergency@ikita.at>");
	    childBean.setAllergies("allergies");
	    childBean.setDefaultBringTime(null);
	    childBean.setDefaultPickupTime(null);
	    childBean.setSex("male");
	    selectedParents.add("<tester@ikita.at>");
	    childBean.setSelectedParents(selectedParents);
	}
	
	@Test
	@DirtiesContext
	@WithMockUser(username = "teacher1@ikita.at", authorities = {"TEACHER"})
	public void testAddDefaultPresences(){
		childBean.setDefaultPresences1(defaultPresences1);
		childBean.addDefaultPresences();
		Assert.assertEquals(days, childBean.getDefaultPresences());
		
	}
	
	@Test
	@DirtiesContext
	@WithMockUser(username = "teacher1@ikita.at", authorities = {"TEACHER"})
	public void testClearSettings(){
		childBean.clearSettings();
		Assert.assertNull(childBean.getLastName());
		Assert.assertNull(childBean.getFirstName());
		Assert.assertNull(childBean.getBirthday());
		Assert.assertNull(childBean.getDefaultBringTime());
		Assert.assertNull(childBean.getDefaultPickupTime());
		Assert.assertNull(childBean.getDeRegistrationDate());
		Assert.assertNull(childBean.getRegistrationDate());
		Assert.assertNull(childBean.getSex());
		Assert.assertNull(childBean.getEmergencyContact1());
	}
	
	@Test
	@DirtiesContext
	@WithMockUser(username = "teacher1@ikita.at", authorities = {"TEACHER"})
	public void testAddEmergencyContract(){
		childBean.setEmergencyContact1("<teacher1@ikita.at>");
		childBean.addEmergencyContact();
		Assert.assertEquals("teacher1@ikita.at", childBean.getEmergencyContact().getEmail());
	}
	
	@Test
	@DirtiesContext
	@WithMockUser(username = "teacher1@ikita.at", authorities = {"TEACHER"})
	public void testAddGuardian(){
		Set<String> guardian1 = new HashSet<>();
		guardian1.add("<teacher1@ikita.at>");
		childBean.setGuardian1(guardian1);
		childBean.addGuardian();
		Person person = personService.loadByEmail("teacher1@ikita.at");
		Set<Person> guardian = new HashSet<>();
		guardian.add(person);
		Assert.assertEquals(guardian, childBean.getGuardian());
	}
	
    @Test
    @DirtiesContext
    @WithMockUser(username = "teacher1@ikita.at", authorities = {"TEACHER"})
    public void testStoreSiblings(){
		childBean.setStoredParents(parents);
    	childBean.storeSiblings();
    	Assert.assertEquals(parent.getChildren(), childBean.getStoredSiblings());
    }
    
	@Test
	@DirtiesContext
	@WithMockUser(username = "teacher1@ikita.at", authorities = {"TEACHER"})
	public void testGender(){
		childBean.addGender();
		Assert.assertEquals(Gender.MALE, childBean.getEnum_gender());
		Assert.assertNotEquals(Gender.FEMALE, childBean.getEnum_gender());
		Assert.assertNotEquals(Gender.OTHER, childBean.getEnum_gender());	
		
		childBean.setSex("female");
		childBean.addGender();
		Assert.assertEquals(Gender.FEMALE, childBean.getEnum_gender());
		Assert.assertNotEquals(Gender.MALE, childBean.getEnum_gender());
		Assert.assertNotEquals(Gender.OTHER, childBean.getEnum_gender());	
		
		childBean.setSex("other");
		childBean.addGender();
		Assert.assertEquals(Gender.OTHER, childBean.getEnum_gender());
		Assert.assertNotEquals(Gender.MALE, childBean.getEnum_gender());
		Assert.assertNotEquals(Gender.FEMALE, childBean.getEnum_gender());
	}
	
    @Test
	@DirtiesContext
	@WithMockUser(username = "teacher1@ikita.at", authorities = {"TEACHER"})
    public void testSelectedParentsStore(){
    	Set<String> selectedParents = new HashSet<> ();
    	selectedParents.add("<test@ikita.at>");
    	
    	Person person = Person.create("test@ikita.at");
    	person.setLastName("Tester");
    	person.setFirstName("Irgendein");
    	Set<Person> storedParents = new HashSet<>();
    	storedParents.add(person);
    	
    	childBean.setSelectedParents(selectedParents);
    	childBean.selectedParentsStore();
    	
    	Assert.assertEquals(storedParents, childBean.getStoredParents());
    }   
}