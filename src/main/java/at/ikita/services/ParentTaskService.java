package at.ikita.services;

import java.util.List;

import at.ikita.model.LogMessageType;
import at.ikita.model.ParentTask;
import at.ikita.model.Person;
import at.ikita.repositories.ParentTaskRepository;
import at.ikita.repositories.PersonRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;


/**
 * Service for accessing and manipulating ParentTask objects.
 *
 * @author Fabio.Valentini@student.uibk.ac.at
 */

@Component
@Scope("application")
public class ParentTaskService extends AbstractService<ParentTask, ParentTaskRepository, Integer> {

    private LogService logService;

    @Autowired
    public ParentTaskService(ParentTaskRepository parentTaskRepository,
                             PersonRepository personRepository,
                             LogService logService) {
        super(parentTaskRepository, personRepository);

        Assert.notNull(logService);
        this.logService = logService;
    }

    @Override
    public void deleteEntry(ParentTask entry) {
        logService.log(LogMessageType.AUDIT, this.getClass(),
                "Action: Delete entry: " + entry.toString());
        repository.delete(entry);
    }

    public List<ParentTask> loadByParent(Person parent) {
        return repository.findByParent(parent);
    }

    public List<ParentTask> searchByTitle(String title) {
        return repository.findByTitle(title);
    }

    public List<ParentTask> loadAllActive() {
        return repository.findActive();
    }

    public List<ParentTask> loadAllInactive() {
        return repository.findInactive();
    }

    public List<ParentTask> loadAllUrgent() {
        return repository.findUrgent();
    }

    public List<ParentTask> loadAllDone() {
        return repository.findAllByDone(true);
    }

    public List<ParentTask> loadAllTodo() {
        return repository.findAllByDone(false);
    }

    public List<ParentTask> loadByPersonAndStatus(Person parent, boolean todo) {
        return repository.findByPersonAndStatus(parent, todo);
    }

}
