package at.ikita.services;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import at.ikita.model.Child;
import at.ikita.model.LogMessageType;
import at.ikita.model.Person;
import at.ikita.model.PersonRole;
import at.ikita.repositories.ChildRepository;
import at.ikita.repositories.PersonRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;


/**
 * Service for accessing and manipulating ParentTask objects.
 *
 * @author Fabio.Valentini@student.uibk.ac.at
 */

@Component
@Scope("application")
public class PersonService extends AbstractService<Person, PersonRepository, Integer> {

    private ChildRepository childRepository;
    private LogService logService;

    @Autowired
    public PersonService(PersonRepository personRepository,
                         ChildRepository childRepository,
                         LogService logService) {
        super(personRepository, personRepository);

        Assert.notNull(childRepository);
        Assert.notNull(logService);

        this.childRepository = childRepository;
        this.logService = logService;

        if (loadAll().isEmpty()) {
            createAdmin();
            logService.log(LogMessageType.AUDIT, this.getClass(),
                    "Created first user 'admin@ikita.at' with password 'passwd' for initial configuration.");
        }
    }

    private void createAdmin() {
        Person admin = Person.create("admin@ikita.at");
        admin.setLastName("Benutzer");
        admin.setFirstName("Administrator");
        admin.setPassword("passwd");
        admin.setEnabled(true);
        admin.setActivated(true);
        admin.addRole(PersonRole.ADMIN);
        saveEntry(admin);
    }

    @Override
    public void deleteEntry(Person entry) {
        logService.log(LogMessageType.AUDIT, this.getClass(),
                "Action: Delete entry: " + entry.toString());

        Set<Child> mustUpdateChildren = new HashSet<>();

        mustUpdateChildren.addAll(entry.getCharges());
        mustUpdateChildren.addAll(entry.getChildren());

        entry.clearChildren();
        entry.clearCharges();

        for (Child child : mustUpdateChildren)
            childRepository.save(child);

        Person newEntry = saveEntry(entry);
        repository.delete(newEntry);
    }

    public List<Person> loadAllByRole(PersonRole role) {
        return repository.findByRole(role);
    }

    public Person loadFromString(String person) {
        String email;

        try {
            email = person.split("<")[1].split(">")[0];
        } catch (IndexOutOfBoundsException e) {
            return null;
        }

        return loadByEmail(email);
    }

    public Person loadByEmail(String email) {
        return repository.findByEmail(email);
    }

    public List<Person> loadAllConfirmedGuardians() {
        return repository.findGuardians(true);
    }

    public List<Person> loadAllUnconfirmedGuardians() {
        return repository.findGuardians(false);
    }

    public List<Person> loadAllActiveParents() {
        return repository.findParents(true);
    }

    public List<Person> loadAllInActiveParents() {
        return repository.findParents(false);
    }

    public List<Person> loadEnabled() {
        return repository.findByEnabled(true);
    }

    public List<Person> loadDisabled() {
        return repository.findByEnabled(false);
    }

    public List<Person> loadActivated() {
        return repository.findByActivated(true);
    }

    public List<Person> loadNotActivated() {
        return repository.findByActivated(false);
    }
}
