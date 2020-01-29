package at.ikita.repositories;

import java.util.Date;

import at.ikita.model.DayCareCalendar;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


/**
 * Repository for managing {@link DayCareCalendar} entities.
 *
 * @author Fabio.Valentini@student.uibk.ac.at
 */

public interface DayCareCalendarRepository extends AbstractRepository<DayCareCalendar, Integer> {

    @Query("SELECT d FROM DayCareCalendar d WHERE d.date = :date")
    DayCareCalendar findByDate(@Param("date") Date date);

    @Query("SELECT d FROM DayCareCalendar d WHERE d.date = (SELECT MAX(d.date) FROM DayCareCalendar d)")
    DayCareCalendar findLastEntry();
}
