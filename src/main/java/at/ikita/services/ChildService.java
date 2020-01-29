package at.ikita.services;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import at.ikita.model.Child;
import at.ikita.model.LogMessageType;
import at.ikita.model.Person;
import at.ikita.repositories.ChildRepository;
import at.ikita.repositories.PersonRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;


/**
 * Service for accessing and manipulating Child objects.
 *
 * @author Fabio.Valentini@student.uibk.ac.at
 */

@Component
@Scope("application")
public class ChildService extends AbstractService<Child, ChildRepository, Integer> {

    private LogService logService;

    @Autowired
    public ChildService(ChildRepository childRepository,
                        PersonRepository personRepository,
                        LogService logService) {
        super(childRepository, personRepository);
        Assert.notNull(logService);
        this.logService = logService;
    }

    @Override
    public Child saveEntry(Child entry) {
        Child testExisting = repository.findByIdentifiers(entry.getLastName(), entry.getFirstName(), entry.getBirthday());

        if (testExisting != null) {
            if (entry.isNew() || !entry.getId().equals(testExisting.getId()))
                throw new DataIntegrityViolationException("Duplicate child detected (with same name and birthday)!");
        }

        return super.saveEntry(entry);
    }

    @Override
    public void deleteEntry(Child entry) {
        logService.log(LogMessageType.AUDIT, this.getClass(),
                "Action: Delete entry: " + entry.toString());

        Set<Person> mustUpdatePersons = new HashSet<>();
        Set<Child> mustUpdateChildren = new HashSet<>();

        mustUpdatePersons.addAll(entry.getParents());
        mustUpdatePersons.addAll(entry.getGuardians());
        mustUpdateChildren.addAll(entry.getSiblings());

        if (entry.getEmergencyContact() != null)
            mustUpdatePersons.add(entry.getEmergencyContact());

        entry.clearParents();
        entry.clearGuardians();
        entry.clearSiblings();
        entry.setEmergencyContact(null);

        for (Person person : mustUpdatePersons)
            personRepository.save(person);

        for (Child child : mustUpdateChildren)
            repository.save(child);

        Child newEntry = saveEntry(entry);
        repository.delete(newEntry);
    }

    public Child loadByNameAndBirthday(String lastName, String firstName, Date birthday) {
        return repository.findByIdentifiers(lastName, firstName, birthday);
    }

    public List<Child> loadByName(String lastName, String firstName) {
        return repository.findByName(lastName, firstName);
    }

    public List<Child> loadByLastName(String lastName) {
        return repository.findByLastName(lastName);
    }

    public List<Child> loadByFirstName(String firstName) {
        return repository.findByFirstName(firstName);
    }

    public List<Child> loadByParent(Person parent) {
        return repository.findByParent(parent);
    }

    public List<Child> loadActive() {
        return repository.findActive();
    }

    public List<Child> loadInactive() {
        return repository.findInactive();
    }

}
