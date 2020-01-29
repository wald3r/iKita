package at.ikita.repositories;

import java.util.List;

import at.ikita.model.Person;
import at.ikita.model.PersonRole;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


/**
 * Repository for managing {@link Person} entities.
 *
 * @author Fabio.Valentini@student.uibk.ac.at
 */

public interface PersonRepository extends AbstractRepository<Person, Integer> {

    @Query("SELECT p FROM Person p WHERE p.email = :email")
    Person findByEmail(@Param("email") String email);

    @Query("SELECT p FROM Person p WHERE p.lastName = :lastName AND p.firstName = :firstName")
    List<Person> findByName(@Param("lastName") String lastName, @Param("firstName") String firstName);

    @Query("SELECT p FROM Person p WHERE :role MEMBER p.roles")
    List<Person> findByRole(@Param("role") PersonRole role);

    @Query("SELECT p FROM Person p WHERE p.enabled = :enabled")
    List<Person> findByEnabled(@Param("enabled") boolean enabled);

    @Query("SELECT p FROM Person p WHERE p.activated = :activated")
    List<Person> findByActivated(@Param("activated") boolean activated);

    @Query("SELECT p FROM Person p WHERE 'GUARDIAN' MEMBER p.roles AND p.activated = :activated")
    List<Person> findGuardians(@Param("activated") boolean activated);

    @Query("SELECT p FROM Person p WHERE 'PARENT' MEMBER p.roles AND p.active = :active")
    List<Person> findParents(@Param("active") boolean active);

}
