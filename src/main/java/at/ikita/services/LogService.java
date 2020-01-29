package at.ikita.services;

import java.io.*;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import at.ikita.configs.AppConfiguration;
import at.ikita.model.LogMessage;
import at.ikita.model.LogMessageType;
import at.ikita.repositories.LogMessageRepository;
import at.ikita.repositories.PersonRepository;

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
public class LogService extends AbstractService<LogMessage, LogMessageRepository, UUID> {

    private AppConfiguration appConfiguration;

    @Autowired
    public LogService(LogMessageRepository pictureRepository,
                      PersonRepository personRepository,
                      AppConfiguration appConfiguration) {
        super(pictureRepository, personRepository);

        Assert.notNull(appConfiguration);
        this.appConfiguration = appConfiguration;
    }

    @Override
    public void deleteEntry(LogMessage message) {
        log(LogMessageType.AUDIT, this.getClass(), "Someone tried to delete a log entry.");
    }

    private void write(String message) {
        String path = appConfiguration.getLogFilePath();

        FileWriter fileWriter;

        try {
            fileWriter = new FileWriter(path, true);
        } catch (IOException e) {
            System.out.println("Log File cannot be opened for writing.");
            e.printStackTrace();
            return;
        }

        try {
            fileWriter.write(message + "\n");
        } catch (IOException e) {
            System.out.println("Log File cannot be written to.");
            e.printStackTrace();
        }

        try {
            fileWriter.close();
        } catch (IOException e) {
            System.out.println("Log File cannot be closed.");
            e.printStackTrace();
        }
    }

    /**
     * This method creates, writes and saves a log message.
     * The generated message is written to System.out, the specified log file, and in the database.
     *
     * @param type    specifies the type of log message (INFO, WARNING, or AUDIT)
     * @param origin  specifies the Class the message is caused by (just use this.getClass())
     * @param message specifies the message body
     * @return returns the saved LogMessage entity (can be mostly ignored)
     */
    public LogMessage log(LogMessageType type, Class origin, String message) {
        LogMessage entry = LogMessage.create(type, origin, message);

        System.out.println(entry);
        write(entry.toString());

        return saveEntry(entry);
    }

    public List<LogMessage> loadByTime(Date after, Date before) {
        return repository.findByTime(after, before);
    }

    public List<LogMessage> loadByType(LogMessageType type) {
        return repository.findByType(type);
    }

    public List<LogMessage> loadByTimeAndType(Date after, Date before, LogMessageType type) {
        return repository.findByTimeAndType(after, before, type);
    }
}
