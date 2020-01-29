package at.ikita.tests.beans;



import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

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
import at.ikita.model.Gender;
import at.ikita.model.Person;
import at.ikita.model.PersonRole;
import at.ikita.model.Picture;
import at.ikita.services.ChildService;
import at.ikita.services.PersonService;
import at.ikita.services.PictureService;
import at.ikita.ui.beans.ChildBean;
import at.ikita.ui.beans.PersonBean;

/**
 * 
 * junit test class for PersonBean
 * for methods storeGuardians() and selectedChildrenStore()
 * 
 * @author kerstin.klabischnig@student.uibk.ac.at
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Main.class)
@WebAppConfiguration
public class StoreGuardianTest {

	@Autowired
	PersonService personService;
	@Autowired
	ChildService childService;
	
	@Autowired
	PersonBean personBean;
	@Autowired
	ChildBean childBean;
	
	Person person;
	Person guardian;
	Child child;
	
	private Set<String> children1 = new HashSet<>();
	private Set<Child> children = new HashSet<>();
	private Date birthday = new GregorianCalendar(02, 02, 2012).getTime();
	private Set<Person> guardians = new HashSet<>();
	
	@Before
	public void setupTestData(){
		child = Child.create("New", "Child", birthday);
		child.setRegistrationDate(
                new GregorianCalendar(2017, 3, 1).getTime());
		child.setDeRegistrationDate(
                new GregorianCalendar(2017, 4, 1).getTime());
		child.setGender(Gender.MALE);
		childService.saveEntry(child);
		
		//create guardian
		Set <PersonRole> roles = new HashSet<>();
        roles.add(PersonRole.GUARDIAN);
        guardian = Person.create("test@guardian.com", "New",
                "Guardian", "test", roles);
		personService.saveEntry(guardian);
		guardians.add(guardian);
		personBean.setPerson(guardian);
	}
	
	@Test
	@DirtiesContext
	@WithMockUser(username = "user02@ikita.at", authorities = {"PARENT"})
	public void testStoreGuardianToChildFail(){
		personBean.selectedChildrenStore();
		personBean.storeGuardians();
		Assert.assertTrue(child.getGuardians().isEmpty());
	}
	
	@Test
	@DirtiesContext
	@WithMockUser(username = "user02@ikita.at", authorities = {"PARENT"})
	public void testStoreGuardianToChild(){
		children1.add("New Child (02.03.2012)");
		personBean.setChildren1(children1);
		personBean.selectedChildrenStore();
		personBean.setPerson(guardian);
		Child child1 = childService.loadByNameAndBirthday("New", "Child", birthday);
		children.remove(child);
		children.add(child1);
		childService.saveEntry(child1);
		personBean.setChildren(children);
		personBean.storeGuardians();
		Assert.assertTrue(child1.getGuardians().contains(guardian));
	}
}	