package at.ikita.services;

import java.util.Date;

import at.ikita.model.DayCareCalendar;
import at.ikita.model.LogMessageType;
import at.ikita.repositories.DayCareCalendarRepository;
import at.ikita.repositories.PersonRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;


/**
 * Service for accessing and manipulating DayCareCalendar objects.
 *
 * @author Fabio.Valentini@student.uibk.ac.at
 */

@Component
@Scope("application")
public class DayCareCalendarService extends AbstractService<DayCareCalendar, DayCareCalendarRepository, Integer> {

    private LogService logService;

    @Autowired
    public DayCareCalendarService(DayCareCalendarRepository dayCareCalendarRepository,
                                  PersonRepository personRepository,
                                  LogService logService) {
        super(dayCareCalendarRepository, personRepository);

        Assert.notNull(logService);
        this.logService = logService;
    }

    @Override
    public DayCareCalendar saveEntry(DayCareCalendar entry) {
        DayCareCalendar testExisting = repository.findByDate(entry.getDate());

        if ((testExisting != null) && entry.isNew())
            throw new DataIntegrityViolationException("Duplicate calendar entry detected (with the same date)!");

        return super.saveEntry(entry);
    }

    public void deleteEntry(DayCareCalendar entry) {
        logService.log(LogMessageType.AUDIT, this.getClass(),
                "Action: Delete entry: " + entry.toString());
        repository.delete(entry);
    }

    public DayCareCalendar loadByDate(Date date) {
        return repository.findByDate(date);
    }

    public DayCareCalendar loadLastEntry() {
        return repository.findLastEntry();
    }

}
