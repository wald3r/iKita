package at.ikita.services;

import java.util.Date;
import java.util.List;

import at.ikita.model.Child;
import at.ikita.model.ChildCalendar;
import at.ikita.model.LogMessageType;
import at.ikita.repositories.ChildCalendarRepository;
import at.ikita.repositories.PersonRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;


/**
 * Service for accessing and manipulating ChildCalendar objects.
 *
 * @author Fabio.Valentini@student.uibk.ac.at
 * @author Andre.Potocnik@student.uibk.ac.at
 */

@Component
@Scope("application")
public class ChildCalendarService extends AbstractService<ChildCalendar, ChildCalendarRepository, Integer> {

    private LogService logService;

    @Autowired
    public ChildCalendarService(ChildCalendarRepository childCalendarRepository,
                                PersonRepository personRepository,
                                LogService logService) {
        super(childCalendarRepository, personRepository);

        Assert.notNull(logService);
        this.logService = logService;
    }

    @Override
    public ChildCalendar saveEntry(ChildCalendar entry) {
        ChildCalendar testExisting = repository.findByChildAndDate(
                entry.getChild(), entry.getDate());

        if ((testExisting != null) && entry.isNew())
            throw new DataIntegrityViolationException("Duplicate calendar entry detected (with same child and date)!");

        return super.saveEntry(entry);
    }

    @Override
    public void deleteEntry(ChildCalendar entry) {
        logService.log(LogMessageType.AUDIT, this.getClass(),
                "Action: Delete entry: " + entry.toString());
        repository.delete(entry);
    }

    public ChildCalendar loadByChildAndDate(Child child, Date date) {
        return repository.findByChildAndDate(child, date);
    }

    public List<ChildCalendar> loadByDate(Date date) {
        return repository.findByDate(date);
    }

    public List<ChildCalendar> loadByChild(Child child) {
        return repository.findByChild(child);
    }

    public List<ChildCalendar> loadByChildAndDateRange(Child child, Date since, Date before) {
        return repository.findByChildAndDateRange(child, since, before);
    }
 }
