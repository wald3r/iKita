package at.ikita.tests.controller;

/* FIXME FIXME FIXME FIXME FIXME FIXME FIXME FIXME FIXME FIXME FIXME FIXME FIXME FIXME FIXME FIXME
import at.ikita.Main;
import at.ikita.model.Child;
import at.ikita.model.Person;
import at.ikita.services.ChildService;
import at.ikita.services.PersonService;
import at.ikita.ui.beans.PersonBean;
import at.ikita.ui.beans.SessionInfoBean;
import at.ikita.ui.controllers.ChildCalendarController;
import org.hamcrest.collection.IsIterableContainingInOrder;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.List;

/**
 * Tests for {@link ChildCalendarController}
 *
 * @author lucas.markovic@student.uibk.ac.at
 *
 * TODO test doesn't work, because getAuthenticatedUser in the Service Classes don't work during tests
 *
 * /

@SuppressWarnings("deprecation")
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Main.class)
@WebAppConfiguration
public class ChildCalendarControllerTest {

    @Autowired
    ChildCalendarController childCalendarController;
    @Autowired
    ChildService childService;
    @Autowired
    PersonService personService;
    @Autowired
    SessionInfoBean sessionInfoBean;
    @Autowired
    PersonBean personBean;


    /*
    Person
    (id, email, last_name, first_name, password, mail_notification, mail_lunch_reminder, enabled, activated)
    (40, 'user39@ikita.at', 'Reiter', 'Thorsten', '$2a$10$n/r9HurPFd9JBE1B6cJIG.6UjfRKgjl23l8NI.Kn/1shetD1QaRJG', TRUE, TRUE, TRUE, TRUE),
    Children
    (id, last_name, first_name, gender, birthday, registration_date, de_registration_date, default_bring_time, default_pickup_time)
    (20, 'Reiter', 'Lisa', 'FEMALE', '2014-12-27', '2017-04-28', '2017-12-31', '10:50:00', '18:00:00'),
    (21, 'Reiter', 'Daniela', 'FEMALE', '2014-12-27', '2017-04-28', '2017-12-31', '10:50:00', '18:00:00');
    Guardians
    (43, 'guardian01@ikita.at', 'Leitner', 'Matthias', '$2a$10$n/r9HurPFd9JBE1B6cJIG.6UjfRKgjl23l8NI.Kn/1shetD1QaRJG', TRUE, TRUE, FALSE, TRUE),
    (44, 'guardian02@ikita.at', 'Fischer', 'Elisabeth', '$2a$10$n/r9HurPFd9JBE1B6cJIG.6UjfRKgjl23l8NI.Kn/1shetD1QaRJG', TRUE, TRUE, FALSE, TRUE),
    * /
    @Test
    @WithMockUser(username = "user39@ikita.at", authorities = {"PARENT"})
    public void testChildCalendarControllerInit()
    {
        //childCalendarController = new ChildCalendarController();
        //childCalendarController.init();

        // logged in user
        //Person user = childCalendarController.getUser();
        //Person user = sessionInfoBean.getCurrentUser();
        //Person user = personService.loadByEmail("user39@ikita.at");
        Person user = personBean.getCurrentPerson();
        Assert.assertEquals(user.getFirstName(), "Thorsten");
        Assert.assertEquals(user.getLastName(), "Reiter");

        // children of user
        //user = personService.loadByEmail("user39@ikita.at");
        List<Child> childrenFromController = childCalendarController.getChildren();
        List<Child> childrenExpected  = childService.loadByParent(user);
        Assert.assertThat(childrenFromController, IsIterableContainingInOrder.contains(childrenExpected.toArray()));
    }

}
*/