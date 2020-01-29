package at.ikita.repositories;

import java.util.Date;
import java.util.List;

import at.ikita.model.Child;
import at.ikita.model.ChildCalendar;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


/**
 * Repository for managing {@link ChildCalendar} entities.
 *
 * @author Fabio.Valentini@student.uibk.ac.at
 */

public interface ChildCalendarRepository extends AbstractRepository<ChildCalendar, Integer> {

    @Query("SELECT c FROM ChildCalendar c WHERE c.child = :child AND c.date = :date")
    ChildCalendar findByChildAndDate(@Param("child") Child child, @Param("date") Date date);

    @Query("SELECT c FROM ChildCalendar c WHERE c.date = :date")
    List<ChildCalendar> findByDate(@Param("date") Date date);

    @Query("SELECT c FROM ChildCalendar c WHERE c.child = :child")
    List<ChildCalendar> findByChild(@Param("child") Child child);

    @Query("SELECT c FROM ChildCalendar c WHERE c.child = :child AND c.date >= :since AND c.date <= :before")
    List<ChildCalendar> findByChildAndDateRange(@Param("child") Child child,
                                                @Param("since") Date since,
                                                @Param("before") Date before);
}
