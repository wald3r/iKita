package at.ikita.repositories;

import java.util.List;

import at.ikita.model.ParentTask;
import at.ikita.model.Person;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Repository for managing {@link ParentTask} entities.
 *
 * @author Fabio.Valentini@student.uibk.ac.at
 */

public interface ParentTaskRepository extends AbstractRepository<ParentTask, Integer> {

    @Query("SELECT p FROM ParentTask p WHERE p.person = :person")
    List<ParentTask> findByParent(@Param("person") Person parent);

    @Query("SELECT p FROM ParentTask p WHERE p.title LIKE CONCAT('%', :title, '%')")
    List<ParentTask> findByTitle(@Param("title") String title);

    @Query("SELECT p FROM ParentTask p WHERE p.startDate <= current_date AND p.endDate >= current_date")
    List<ParentTask> findActive();

    @Query("SELECT p FROM ParentTask p WHERE p.startDate > current_date OR p.endDate < current_date")
    List<ParentTask> findInactive();

    @Query("SELECT p FROM ParentTask p WHERE p.urgent = true")
    List<ParentTask> findUrgent();

    @Query("SELECT p FROM ParentTask p WHERE p.done = :done")
    List<ParentTask> findAllByDone(@Param("done") boolean done);

    @Query("SELECT p FROM ParentTask p WHERE p.person = :parent AND p.done = :done")
    List<ParentTask> findByPersonAndStatus(@Param("parent") Person parent, @Param("done") boolean done);
}
