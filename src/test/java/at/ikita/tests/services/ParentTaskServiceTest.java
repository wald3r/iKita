package at.ikita.tests.services;

import java.util.*;

import at.ikita.Main;
import at.ikita.model.ParentTask;
import at.ikita.model.Person;
import at.ikita.services.ParentTaskService;
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
 * Tests for {@link ParentTaskService}
 *
 * @author Fabio.Valentini@student.uibk.ac.at
 */

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Main.class)
@WebAppConfiguration
public class ParentTaskServiceTest {

    @Autowired
    private ParentTaskService parentTaskService;

    @Autowired
    private PersonService personService;

    private ParentTask testTaskNew1;
    private ParentTask testTaskNew2;


    @Before
    public void setupTestData() {
        testTaskNew1 = ParentTask.create(personService.loadEntry(2), "Test Task",
                new GregorianCalendar(2017, 6, 2).getTime(),
                new GregorianCalendar(2017, 6, 2).getTime());

        testTaskNew2 = ParentTask.create("Test Task",
                new GregorianCalendar(2017, 6, 3).getTime(),
                new GregorianCalendar(2017, 6, 4).getTime());
        testTaskNew2.setPerson(personService.loadEntry(3));
    }

    @Test
    @WithMockUser(username = "admin@ikita.at", authorities = {"ADMIN"})
    public void testLoadTask() {
        ParentTask entry = parentTaskService.loadEntry(1);
        Assert.assertNotNull(entry);
    }

    @Test
    @DirtiesContext
    @WithMockUser(username = "admin@ikita.at", authorities = {"ADMIN"})
    public void testSaveTask() {
        ParentTask entry = parentTaskService.saveEntry(testTaskNew1);
        Assert.assertTrue(testTaskNew1.equals(entry));
        Assert.assertNotEquals(0, (long) entry.getId());
    }

    @Test
    @DirtiesContext
    @WithMockUser(username = "admin@ikita.at", authorities = {"ADMIN"})
    public void testDeleteTask() {
        ParentTask entry = parentTaskService.loadEntry(2);
        Assert.assertNotNull(entry);

        parentTaskService.deleteEntry(entry);

        entry = parentTaskService.loadEntry(2);
        Assert.assertNull(entry);
    }

    @Test
    @DirtiesContext
    @WithMockUser(username = "admin@ikita.at", authorities = {"ADMIN"})
    public void testUpdateTask() {
        ParentTask entry = parentTaskService.loadEntry(1);
        Assert.assertNotNull(entry);

        Person newPerson = personService.loadEntry(9);
        Date newStartDate = new GregorianCalendar(2017, 0, 1).getTime();
        Date newEndDate = new GregorianCalendar(2017, 11, 31).getTime();
        boolean newUrgent = !entry.isUrgent();
        boolean newDone = !entry.isDone();
        String newTitle = "Buchstaben zählen";
        String newDescription = "ABCDEFGHIJKLMNOPQRSTUVWXYZ = 26";

        entry.setPerson(newPerson);
        entry.setStartDate(newStartDate);
        entry.setEndDate(newEndDate);
        entry.setUrgent(newUrgent);
        entry.setDone(newDone);
        entry.setTitle(newTitle);
        entry.setDescription(newDescription);

        ParentTask newEntry = parentTaskService.saveEntry(entry);

        Assert.assertEquals(newPerson, newEntry.getPerson());
        Assert.assertEquals(newStartDate, newEntry.getStartDate());
        Assert.assertEquals(newEndDate, newEntry.getEndDate());
        Assert.assertEquals(newUrgent, newEntry.isUrgent());
        Assert.assertEquals(newDone, newEntry.isDone());
        Assert.assertEquals(newTitle, newEntry.getTitle());
        Assert.assertEquals(newDescription, newEntry.getDescription());
        Assert.assertTrue(entry.equals(newEntry));
    }

    @Test
    @DirtiesContext
    @WithMockUser(username = "admin@ikita.at", authorities = {"ADMIN"})
    public void testNumberSanity() {
        Collection<ParentTask> state0 = parentTaskService.loadAll();
        parentTaskService.saveEntry(testTaskNew2);
        Collection<ParentTask> state1 = parentTaskService.loadAll();
        parentTaskService.deleteEntry(testTaskNew2);
        Collection<ParentTask> state2 = parentTaskService.loadAll();

        Assert.assertEquals(state0.size(), state1.size() - 1);
        Assert.assertEquals(state1.size(), state2.size() + 1);
        Assert.assertEquals(state0.size(), state2.size());
    }

    @Test
    @WithMockUser(username = "admin@ikita.at", authorities = {"ADMIN"})
    public void failLoadTask() {
        ParentTask entry = parentTaskService.loadEntry(999999);
        Assert.assertNull(entry);
    }

    @Test
    @WithMockUser(username = "admin@ikita.at", authorities = {"ADMIN"})
    public void testLoadByParent() {
        for (Person person : personService.loadAll()) {
            Collection<ParentTask> entries = parentTaskService.loadByParent(person);

            for (ParentTask entry : entries)
                Assert.assertEquals(person, entry.getPerson());
        }
    }

    @Test
    @WithMockUser(username = "admin@ikita.at", authorities = {"ADMIN"})
    public void testSearchByTitle() {
        String query = "Feuer";
        Collection<ParentTask> entries = parentTaskService.searchByTitle(query);

        for (ParentTask entry : entries)
            Assert.assertTrue(entry.getTitle().contains(query));
    }

    @Test
    @WithMockUser(username = "admin@ikita.at", authorities = {"ADMIN"})
    public void testLoadByUrgent() {
        Collection<ParentTask> entries = parentTaskService.loadAllUrgent();

        for (ParentTask entry : entries)
            Assert.assertTrue(entry.isUrgent());
    }

    @Test
    @WithMockUser(username = "admin@ikita.at", authorities = {"ADMIN"})
    public void testLoadByActive() {
        Collection<ParentTask> entries_active = parentTaskService.loadAllActive();
        Collection<ParentTask> entries_inactive = parentTaskService.loadAllInactive();

        for (ParentTask entry : entries_active)
            Assert.assertTrue(entry.isActive());

        for (ParentTask entry : entries_inactive)
            Assert.assertFalse(entry.isActive());

        Assert.assertEquals(parentTaskService.loadAll().size(),
                entries_active.size() + entries_inactive.size());
    }

    @Test
    @WithMockUser(username = "admin@ikita.at", authorities = {"ADMIN"})
    public void testLoadByDone() {
        Collection<ParentTask> entries_done = parentTaskService.loadAllDone();
        Collection<ParentTask> entries_todo = parentTaskService.loadAllTodo();

        for (ParentTask entry : entries_done)
            Assert.assertTrue(entry.isDone());

        for (ParentTask entry : entries_todo)
            Assert.assertFalse(entry.isDone());

        Assert.assertEquals(parentTaskService.loadAll().size(),
                entries_done.size() + entries_todo.size());
    }
}
