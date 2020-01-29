package at.ikita.services;

import at.ikita.model.Child;
import at.ikita.model.Person;
import at.ikita.model.PersonRole;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;


/**
 * Service that deactivates Users that no longer have active Children within the System
 *
 * @author Fabio.Valentini@student.uibk.ac.at
 */

@Component
@Scope("application")
public class PersonDeactivationService {

    private PersonService personService;

    @Autowired
    public PersonDeactivationService(PersonService personService) {
        Assert.notNull(personService);
        this.personService = personService;
    }

    private static boolean hasActiveChildren(Person person) {
        for (Child child : person.getChildren())
            if (child.isActive())
                return true;

        return false;
    }

    public void updatePersonStatus() {
        for (Person person : personService.loadAllByRole(PersonRole.PARENT))
            person.setActive(hasActiveChildren(person));
    }

}
