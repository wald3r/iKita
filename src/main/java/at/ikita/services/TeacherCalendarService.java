package at.ikita.services;

import java.util.Date;
import java.util.List;

import at.ikita.model.LogMessageType;
import at.ikita.model.Person;
import at.ikita.model.TeacherCalendar;
import at.ikita.repositories.PersonRepository;
import at.ikita.repositories.TeacherCalendarRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;


/**
 * Service for accessing and manipulating TeacherCalendar objects.
 *
 * @author Fabio.Valentini@student.uibk.ac.at
 * @author Andre.Potocnik@student.uibk.ac.at
 */

@Component
@Scope("application")
public class TeacherCalendarService extends AbstractService<TeacherCalendar, TeacherCalendarRepository, Integer> {

    private LogService logService;

    @Autowired
    public TeacherCalendarService(TeacherCalendarRepository teacherCalendarRepository,
                                  PersonRepository personRepository,
                                  LogService logService) {
        super(teacherCalendarRepository, personRepository);

        Assert.notNull(logService);
        this.logService = logService;
    }

    @Override
    public TeacherCalendar saveEntry(TeacherCalendar entry) {
        TeacherCalendar testExisting = repository.findByDateAndTeacher(entry.getDate(), entry.getTeacher());

        if ((testExisting != null) && entry.isNew())
            throw new DataIntegrityViolationException("Duplicate Teacher Calendar entry (with same date and teacher)!");

        return super.saveEntry(entry);
    }

    @Override
    public void deleteEntry(TeacherCalendar entry) {
        logService.log(LogMessageType.AUDIT, this.getClass(),
                "Action: Delete entry: " + entry.toString());
        repository.delete(entry);
    }

    public List<TeacherCalendar> loadByDate(Date date) {
        return repository.findByDate(date);
    }

    public List<TeacherCalendar> loadByTeacher(Person teacher) {
        return repository.findByTeacher(teacher);
    }
}
