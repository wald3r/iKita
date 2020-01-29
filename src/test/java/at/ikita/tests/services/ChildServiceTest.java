package at.ikita.tests.services;

import java.util.*;

import at.ikita.Main;
import at.ikita.model.Child;
import at.ikita.model.Gender;
import at.ikita.model.Person;
import at.ikita.model.PersonRole;
import at.ikita.services.ChildService;
import at.ikita.services.PersonService;

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


/**
 * Tests for {@link ChildService}
 *
 * @author Fabio.Valentini@student.uibk.ac.at
 */

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Main.class)
@WebAppConfiguration
public class ChildServiceTest {

    @Autowired
    private ChildService childService;

    @Autowired
    PersonService personService;

    private Child testChildNew1;
    private Child testChildNew2;
    private Child testChildDuplicate;


    @Before
    public void setupTestData() {
        Date birthday1 = new GregorianCalendar(2000, 11, 23).getTime();
        Date birthday2 = new GregorianCalendar(2012, 11, 23).getTime();
        Date birthday3 = new GregorianCalendar(2014, 3, 8).getTime();

        testChildNew1 = Child.create("Muster", "Max", birthday1);
        testChildNew2 = Child.create("Kind", "Test", birthday2);
        testChildDuplicate = Child.create("Bauer", "Hannah", birthday3);

        testChildNew1.setRegistrationDate(
                new GregorianCalendar(2017, 3, 1).getTime());
        testChildNew1.setDeRegistrationDate(
                new GregorianCalendar(2017, 4, 1).getTime());

        testChildNew2.setRegistrationDate(
                new GregorianCalendar(2017, 3, 1).getTime());
        testChildNew2.setDeRegistrationDate(
                new GregorianCalendar(2017, 4, 1).getTime());

        testChildDuplicate.setRegistrationDate(
                new GregorianCalendar(2017, 3, 1).getTime());
        testChildDuplicate.setDeRegistrationDate(
                new GregorianCalendar(2017, 4, 1).getTime());

        testChildNew1.setGender(Gender.MALE);
        testChildNew2.setGender(Gender.OTHER);
        testChildDuplicate.setGender(Gender.FEMALE);
    }

    @Test
    @WithMockUser(username = "admin@ikita.at", authorities = {"ADMIN"})
    public void testLoadChild() {
        Child entry = childService.loadByNameAndBirthday("Schmid", "Laura",
                new GregorianCalendar(2014, 5, 19).getTime());
        Assert.assertNotNull(entry);
    }

    @Test
    @WithMockUser(username = "admin@ikita.at", authorities = {"ADMIN"})
    public void testLoadChildByName() {
        List<Child> test1 = childService.loadByName("Berger", "Florian");
        Assert.assertEquals(1, test1.size());
        Assert.assertEquals("Berger", test1.get(0).getLastName());
        Assert.assertEquals("Florian", test1.get(0).getFirstName());

        List<Child> test2 = childService.loadByName("Winkler", "Fabian");
        Assert.assertEquals(1, test2.size());
        Assert.assertEquals("Winkler", test2.get(0).getLastName());
        Assert.assertEquals("Fabian", test2.get(0).getFirstName());
    }

    @Test
    @WithMockUser(username = "admin@ikita.at", authorities = {"ADMIN"})
    public void testLoadChildByLastName() {
        Collection<Child> children = childService.loadByLastName("Reiter");
        Assert.assertEquals(2, children.size());

        for (Child child : children)
            Assert.assertEquals("Reiter", child.getLastName());
    }

    @Test
    @WithMockUser(username = "admin@ikita.at", authorities = {"ADMIN"})
    public void testLoadChildByFirstName() {
        Collection<Child> children = childService.loadByFirstName("Anna");
        Assert.assertEquals(1, children.size());

        for (Child child : children)
            Assert.assertEquals("Anna", child.getFirstName());
    }

    @Test
    @WithMockUser(username = "admin@ikita.at", authorities = {"ADMIN"})
    public void testLoadChildByParent() {
        Collection<Person> parents = personService.loadAllByRole(PersonRole.PARENT);

        for (Person parent : parents) {
            Collection<Child> children = childService.loadByParent(parent);

            for (Child child : children)
                Assert.assertTrue(child.getParents().contains(parent));
        }
    }

    @Test
    @WithMockUser(username = "admin@ikita.at", authorities = {"ADMIN"})
    public void testLoadActive() {
        Collection<Child> children = childService.loadActive();

        for (Child child : children)
            Assert.assertTrue(child.isActive());
    }

    @Test
    @WithMockUser(username = "admin@ikita.at", authorities = {"ADMIN"})
    public void testLoadInactive() {
        Collection<Child> children = childService.loadInactive();

        for (Child child : children)
            Assert.assertFalse(child.isActive());
    }

    @Test
    @DirtiesContext
    @WithMockUser(username = "admin@ikita.at", authorities = {"ADMIN"})
    public void testSaveChild() {
        Child entry = childService.saveEntry(testChildNew1);
        Assert.assertTrue(testChildNew1.equals(entry));
        Assert.assertNotEquals(0, (long) entry.getId());
    }

    @Test
    @DirtiesContext
    @WithMockUser(username = "admin@ikita.at", authorities = {"ADMIN"})
    public void testDeleteChild() {
        Child test = childService.loadByNameAndBirthday("Fuchs", "Katharina",
                new GregorianCalendar(2014, 7, 25).getTime());
        Assert.assertNotNull(test);

        childService.deleteEntry(test);

        test = childService.loadByNameAndBirthday("Fuchs", "Katharina",
                new GregorianCalendar(2014, 7, 25).getTime());
        Assert.assertNull(test);
    }

    @Test
    @DirtiesContext
    @WithMockUser(username = "admin@ikita.at", authorities = {"ADMIN"})
    public void testUpdateChild() {
        Child entry = childService.loadByNameAndBirthday("Schwarz", "Sophie",
                new GregorianCalendar(2014, 10, 2).getTime());
        Assert.assertNotNull(entry);

        entry.setLastName("Schwarz-Piech");
        entry.setFirstName("Sophie Anna");

        Child newEntry = childService.saveEntry(entry);

        Assert.assertEquals("Schwarz-Piech", newEntry.getLastName());
        Assert.assertEquals("Sophie Anna", newEntry.getFirstName());

        Assert.assertTrue(entry.equals(newEntry));
    }

    @Test
    @DirtiesContext
    @WithMockUser(username = "admin@ikita.at", authorities = {"ADMIN"})
    public void testNumberSanity() {
        Collection<Child> state0 = childService.loadAll();
        Child savedChild = childService.saveEntry(testChildNew2);
        Collection<Child> state1 = childService.loadAll();
        childService.deleteEntry(savedChild);
        Collection<Child> state2 = childService.loadAll();

        Assert.assertEquals(state0.size(), state1.size() - 1);
        Assert.assertEquals(state1.size(), state2.size() + 1);
        Assert.assertEquals(state0.size(), state2.size());
    }

    @Test
    @WithMockUser(username = "admin@ikita.at", authorities = {"ADMIN"})
    public void failLoadChild() {
        Child entry = childService.loadByNameAndBirthday("Nachname", "Vorname",
                new GregorianCalendar(2000, 0, 1).getTime());
        Assert.assertNull(entry);
    }

    @Test(expected = org.springframework.dao.DataIntegrityViolationException.class)
    @WithMockUser(username = "admin@ikita.at", authorities = {"ADMIN"})
    public void failSaveChild() {
        childService.saveEntry(testChildDuplicate);
    }

}
