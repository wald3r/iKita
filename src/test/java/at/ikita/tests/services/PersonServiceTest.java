package at.ikita.tests.services;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import at.ikita.Main;
import at.ikita.model.Person;
import at.ikita.model.PersonRole;
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
 * Tests for {@link PersonService}
 *
 * @author Fabio.Valentini@student.uibk.ac.at
 */

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Main.class)
@WebAppConfiguration
public class PersonServiceTest {

    @Autowired
    private PersonService personService;

    private Person testPersonNew1;
    private Person testPersonNew2;
    private Person testPersonDuplicate;


    @Before
    public void setupTestData() {
        Set<PersonRole> roles = new HashSet<>();
        roles.add(PersonRole.PARENT);

        testPersonNew1 = Person.create("test@example.com", "Mustermann",
                "Max", "test", roles);

        testPersonNew2 = Person.create("number@test.com", "Nummer",
                "Korrekte", "test", roles);

        testPersonDuplicate = Person.create("admin@ikita.at", "Istrator",
                "Admin", "test", roles);
    }

    @Test
    @WithMockUser(username = "admin@ikita.at", authorities = {"ADMIN"})
    public void testLoadPerson() {
        Person entry = personService.loadByEmail("admin@ikita.at");
        Assert.assertNotNull(entry);
    }

    @Test
    @WithMockUser(username = "admin@ikita.at", authorities = {"ADMIN"})
    public void testLoadByRole() {
        List<Person> admins = personService.loadAllByRole(PersonRole.ADMIN);
        List<Person> parents = personService.loadAllByRole(PersonRole.PARENT);
        List<Person> teachers = personService.loadAllByRole(PersonRole.TEACHER);
        List<Person> guardians = personService.loadAllByRole(PersonRole.GUARDIAN);

        Person admin = personService.loadByEmail("admin@ikita.at");
        Person teacher1 = personService.loadByEmail("teacher1@ikita.at");
        Person teacher2 = personService.loadByEmail("teacher2@ikita.at");
        Person parent1 = personService.loadByEmail("user01@ikita.at");
        Person parent2 = personService.loadByEmail("user02@ikita.at");
        Person guardian1 = personService.loadByEmail("guardian01@ikita.at");
        Person guardian2 = personService.loadByEmail("guardian02@ikita.at");

        Assert.assertTrue(admin.getRoles().contains(PersonRole.ADMIN));

        for (Person teacher : teachers)
            Assert.assertTrue(teacher.getRoles().contains(PersonRole.TEACHER));

        for (Person parent : parents)
            Assert.assertTrue(parent.getRoles().contains(PersonRole.PARENT));

        for (Person guardian : guardians)
            Assert.assertTrue(guardian.getRoles().contains(PersonRole.GUARDIAN));

        Assert.assertTrue(admins.contains(admin));

        Assert.assertTrue(teachers.contains(teacher1));
        Assert.assertTrue(teachers.contains(teacher2));

        Assert.assertTrue(parents.contains(parent1));
        Assert.assertTrue(parents.contains(parent2));

        Assert.assertTrue(guardians.contains(guardian1));
        Assert.assertTrue(guardians.contains(guardian2));

        Assert.assertFalse(admins.contains(parent1));
        Assert.assertFalse(teachers.contains(parent1));
        Assert.assertFalse(parents.contains(admin));
    }

    @Test
    @WithMockUser(username = "admin@ikita.at", authorities = {"ADMIN"})
    public void testLoadGuardiansByActivation() {
        Collection<Person> guardians = personService.loadAllByRole(PersonRole.GUARDIAN);
        Collection<Person> guardians_active = personService.loadAllConfirmedGuardians();
        Collection<Person> guardians_inactive = personService.loadAllUnconfirmedGuardians();

        for (Person guardian : guardians_active)
            Assert.assertTrue(guardian.isActivated());

        for (Person guardian : guardians_inactive)
            Assert.assertFalse(guardian.isActivated());

        Assert.assertEquals(guardians.size(),
                guardians_active.size() + guardians_inactive.size());
    }

    @Test
    @WithMockUser(username = "admin@ikita.at", authorities = {"ADMIN"})
    public void testLoadPersonByEnabled() {
        Collection<Person> persons = personService.loadEnabled();

        for (Person person : persons)
            Assert.assertTrue(person.isEnabled());
    }

    @Test
    @WithMockUser(username = "admin@ikita.at", authorities = {"ADMIN"})
    public void testLoadPersonByDisabled() {
        Collection<Person> persons = personService.loadDisabled();

        for (Person person : persons)
            Assert.assertFalse(person.isEnabled());
    }

    @Test
    @WithMockUser(username = "admin@ikita.at", authorities = {"ADMIN"})
    public void testLoadPersonByActivated() {
        Collection<Person> persons_activated = personService.loadActivated();
        Collection<Person> persons_not_activated = personService.loadNotActivated();

        for (Person person : persons_activated)
            Assert.assertTrue(person.isActivated());

        for (Person person : persons_not_activated)
            Assert.assertFalse(person.isActivated());
    }

    @Test
    @DirtiesContext
    @WithMockUser(username = "admin@ikita.at", authorities = {"ADMIN"})
    public void testSavePerson() {
        Person entry = personService.saveEntry(testPersonNew1);
        Assert.assertTrue(testPersonNew1.equals(entry));
        Assert.assertNotEquals(0, (long) entry.getId());
    }

    @Test
    @DirtiesContext
    @WithMockUser(username = "admin@ikita.at", authorities = {"ADMIN"})
    public void testDeletePerson() {
        Person test = personService.loadByEmail("test@ikita.at");
        Assert.assertNotNull(test);

        personService.deleteEntry(test);

        test = personService.loadByEmail("test@ikita.at");
        Assert.assertNull(test);
    }

    @Test
    @DirtiesContext
    @WithMockUser(username = "admin@ikita.at", authorities = {"ADMIN"})
    public void testUpdatePerson() {
        Person entry = personService.loadByEmail("user03@ikita.at");
        Assert.assertNotNull(entry);

        String newLastName = "Huber-Meier";
        String newFirstName = "Michael Simon";

        entry.setLastName(newLastName);
        entry.setFirstName(newFirstName);

        Person newEntry = personService.saveEntry(entry);

        Assert.assertEquals(newLastName, newEntry.getLastName());
        Assert.assertEquals(newFirstName, newEntry.getFirstName());

        Assert.assertTrue(entry.equals(newEntry));
    }

    @Test
    @DirtiesContext
    @WithMockUser(username = "admin@ikita.at", authorities = {"ADMIN"})
    public void testNumberSanity() {
        Collection<Person> state0 = personService.loadAll();
        testPersonNew2 = personService.saveEntry(testPersonNew2);
        Collection<Person> state1 = personService.loadAll();
        personService.deleteEntry(testPersonNew2);
        Collection<Person> state2 = personService.loadAll();

        Assert.assertEquals(state0.size(), state1.size() - 1);
        Assert.assertEquals(state1.size(), state2.size() + 1);
        Assert.assertEquals(state0.size(), state2.size());
    }

    @Test
    @WithMockUser(username = "admin@ikita.at", authorities = {"ADMIN"})
    public void failLoadPerson() {
        Person entry = personService.loadByEmail("doesntexist@example.com");
        Assert.assertNull(entry);
    }

    @Test(expected = org.springframework.dao.DataIntegrityViolationException.class)
    @WithMockUser(username = "admin@ikita.at", authorities = {"ADMIN"})
    public void failSavePerson() {
        personService.saveEntry(testPersonDuplicate);
    }

    @Test
    @WithMockUser(username = "admin@ikita.at", authorities = {"ADMIN"})
    public void testGetAuthenticatedUser() {
        Person admin = personService.getAuthenticatedUser();

        Assert.assertEquals("admin@ikita.at", admin.getEmail());
        Assert.assertTrue(admin.getRoles().contains(PersonRole.ADMIN));
    }

}
