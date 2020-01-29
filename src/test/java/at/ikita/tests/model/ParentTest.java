package at.ikita.tests.model;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Set;

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
 * Tests for the Child-Parent Relationship between {@link Child} and {@link Person}
 *
 * @author Fabio.Valentini@student.uibk.ac.at
 */

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Main.class)
@WebAppConfiguration
public class ParentTest {

    @Autowired
    private ChildService childService;

    @Autowired
    private PersonService personService;

    private Child testChild;
    private Person testParent1;
    private Person testParent2;

    @Before
    public void setupTestData() {
        Date birthday = new GregorianCalendar(2000, 11, 23).getTime();
        testChild = Child.create("Muster", "Max", birthday);

        testChild.setRegistrationDate(
                new GregorianCalendar(2017, 3, 1).getTime());
        testChild.setDeRegistrationDate(
                new GregorianCalendar(2017, 4, 1).getTime());

        testChild.setGender(Gender.MALE);

        Set<PersonRole> roles = new HashSet<>();
        roles.add(PersonRole.PARENT);

        testParent1 = Person.create("parent1@ikita.at", "Mustermann", "Max",
                "passwd", roles);
        testParent2 = Person.create("parent2@ikita.at", "Mustermann", "Martina",
                "passwd", roles);
    }

    @Test
    @DirtiesContext
    @WithMockUser(username = "admin@ikita.at", authorities = {"ADMIN"})
    public void testSetParent() {
        Child child = childService.saveEntry(testChild);
        Person parent1 = personService.saveEntry(testParent1);
        Person parent2 = personService.saveEntry(testParent2);

        child.addParent(parent1);
        child.addParent(parent2);

        child = childService.saveEntry(child);

        Assert.assertTrue(child.getParents().contains(parent1));
        Assert.assertTrue(child.getParents().contains(parent2));

        Assert.assertTrue(parent1.getChildren().contains(child));
        Assert.assertTrue(parent2.getChildren().contains(child));
    }


    @Test
    @DirtiesContext
    @WithMockUser(username = "admin@ikita.at", authorities = {"ADMIN"})
    public void testSetChild() {
        Child child = childService.saveEntry(testChild);
        Person parent1 = personService.saveEntry(testParent1);
        Person parent2 = personService.saveEntry(testParent2);

        parent1.addChild(child);
        parent2.addChild(child);

        parent1 = personService.saveEntry(parent1);
        parent2 = personService.saveEntry(parent2);

        Assert.assertTrue(parent1.getChildren().contains(child));
        Assert.assertTrue(parent2.getChildren().contains(child));

        Assert.assertTrue(child.getParents().contains(parent1));
        Assert.assertTrue(child.getParents().contains(parent2));
    }

}
