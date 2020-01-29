package at.ikita.repositories;

import java.util.Date;
import java.util.List;

import at.ikita.model.Child;
import at.ikita.model.Person;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


/**
 * Repository for managing {@link Child} entities.
 *
 * @author Fabio.Valentini@student.uibk.ac.at
 */

public interface ChildRepository extends AbstractRepository<Child, Integer> {

    @Query("SELECT c FROM Child c WHERE c.id = :id")
    Child findById(@Param("id") int id);

    @Query("SELECT c FROM Child c WHERE c.lastName = :lastName AND c.firstName = :firstName AND c.birthday = :birthday")
    Child findByIdentifiers(@Param("lastName") String lastName, @Param("firstName") String firstName, @Param("birthday") Date birthday);

    @Query("SELECT c FROM Child c WHERE c.lastName = :lastName AND c.firstName = :firstName")
    List<Child> findByName(@Param("lastName") String lastName, @Param("firstName") String firstName);

    @Query("SELECT c FROM Child c WHERE c.lastName = :lastName")
    List<Child> findByLastName(@Param("lastName") String lastName);

    @Query("SELECT c FROM Child c WHERE c.firstName = :firstName")
    List<Child> findByFirstName(@Param("firstName") String firstName);

    @Query("SELECT c FROM Child c WHERE :parent MEMBER c.parents")
    List<Child> findByParent(@Param("parent") Person parent);

    @Query("SELECT c FROM Child c WHERE c.registrationDate <= current_date AND current_date <= c.deRegistrationDate")
    List<Child> findActive();

    @Query("SELECT c FROM Child c WHERE c.registrationDate > current_date OR current_date > c.deRegistrationDate")
    List<Child> findInactive();

}
