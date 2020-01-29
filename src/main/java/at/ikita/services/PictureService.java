package at.ikita.services;

import at.ikita.model.LogMessageType;
import at.ikita.model.Picture;
import at.ikita.repositories.PersonRepository;
import at.ikita.repositories.PictureRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;


/**
 * Service for accessing and manipulating Picture objects.
 *
 * @author Fabio.Valentini@student.uibk.ac.at
 */

@Component
@Scope("application")
public class PictureService extends AbstractService<Picture, PictureRepository, Integer> {

    private LogService logService;

    @Autowired
    public PictureService(PictureRepository pictureRepository,
                          PersonRepository personRepository,
                          LogService logService) {
        super(pictureRepository, personRepository);

        Assert.notNull(logService);
        this.logService = logService;
    }

    @Override
    public void deleteEntry(Picture entry) {
        logService.log(LogMessageType.AUDIT, this.getClass(),
                "Action: Delete entry: " + entry.toString());
        repository.delete(entry);
    }

    public Picture loadByURI(String URI) {
        return repository.findByURI(URI);
    }
}
