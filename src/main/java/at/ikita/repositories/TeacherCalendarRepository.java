package at.ikita.repositories;

import java.util.Date;
import java.util.List;

import at.ikita.model.Person;
import at.ikita.model.TeacherCalendar;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


/**
 * Repository for managing {@link TeacherCalendar} entities.
 *
 * @author Fabio.Valentini@student.uibk.ac.at
 */

public interface TeacherCalendarRepository extends AbstractRepository<TeacherCalendar, Integer> {

    @Query("SELECT t FROM TeacherCalendar t WHERE t.date = :date AND t.teacher = :teacher")
    TeacherCalendar findByDateAndTeacher(@Param("date") Date date, @Param("teacher") Person teacher);

    @Query("SELECT t FROM TeacherCalendar t WHERE t.date = :date")
    List<TeacherCalendar> findByDate(@Param("date") Date date);

    @Query("SELECT t FROM TeacherCalendar t WHERE t.teacher = :teacher")
    List<TeacherCalendar> findByTeacher(@Param("teacher") Person teacher);

}
