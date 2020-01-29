package at.ikita.services;

import java.io.Serializable;
import java.util.Collection;

import at.ikita.model.Person;
import at.ikita.repositories.AbstractRepository;
import at.ikita.repositories.PersonRepository;

import org.springframework.data.domain.Persistable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.Assert;


/**
 * Abstract Service providing common functionality
 *
 * @author Fabio.Valentini@student.uibk.ac.at
 */

public abstract class AbstractService<E extends Persistable<ID>,
        R extends AbstractRepository<E, ID>,
        ID extends Serializable> {

    PersonRepository personRepository;
    R repository;

    AbstractService(R repository, PersonRepository personRepository) {
        Assert.notNull(repository);
        Assert.notNull(personRepository);

        this.repository = repository;
        this.personRepository = personRepository;
    }

    public abstract void deleteEntry(E entry);

    // Override this method in the subclasses if authorization is desired.
    // @PreAuthorize(SOMETHING)
    public Collection<E> loadAll() {
        return repository.findAll();
    }

    // Override this method in the subclasses if authorization is desired.
    // @PreAuthorize(SOMETHING)
    public E loadEntry(ID id) {
        return repository.findOne(id);
    }

    // Override this method in the subclasses if authorization is desired.
    // @PreAuthorize(SOMETHING)
    public E saveEntry(E entity) {
        E entry = repository.save(entity);
        return repository.findOne(entry.getId());
    }

    public Person getAuthenticatedUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return personRepository.findByEmail(auth.getName());
    }
}
