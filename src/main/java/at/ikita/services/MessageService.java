package at.ikita.services;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import at.ikita.model.LogMessageType;
import at.ikita.model.Message;
import at.ikita.model.Person;
import at.ikita.repositories.MessageRepository;
import at.ikita.repositories.PersonRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;


/**
 * Service for accessing and manipulating {@link Message} objects.
 *
 * @author Fabio.Valentini@student.uibk.ac.at
 */

@Component
@Scope("application")
public class MessageService extends AbstractService<Message, MessageRepository, UUID> {

    private LogService logService;

    @Autowired
    public MessageService(MessageRepository messageRepository,
                          PersonRepository personRepository,
                          LogService logService) {
        super(messageRepository, personRepository);

        Assert.notNull(logService);
        this.logService = logService;
    }

    @Override
    public void deleteEntry(Message message) {
        logService.log(LogMessageType.AUDIT, this.getClass(),
                "Action: Delete entry: " + message.getId());
        repository.delete(message);
    }

    public Message sendMessage(Person recipient, String subject, String content) {
        Message message = Message.create(getAuthenticatedUser(), recipient, subject, content);
        message.setTime(new Date());
        saveEntry(message);
        return message;
    }

    public Message sendResponse(Message original, String content) {
        Message response = Message.createResponse(getAuthenticatedUser(), original, content);
        response.setTime(new Date());
        saveEntry(response);
        return response;
    }

    public Message sendBroadcast(String subject, String content) {
        Message message = Message.createBroadcast(getAuthenticatedUser(), subject, content);
        message.setTime(new Date());
        saveEntry(message);
        return message;
    }

    public List<Message> loadBySender(Person sender) {
        return repository.findBySender(sender);
    }

    public List<Message> loadByRecipient(Person recipient) {
        return repository.findByRecipient(recipient);
    }

    public List<Message> loadBroadcasts() {
        return repository.findBroadcasts(false);
    }

    public List<Message> loadBySubject(String subject) {
        return repository.findBySubject(subject);
    }

    public List<Message> loadMyReadMessages() {
        return repository.findByRecipientAndRead(getAuthenticatedUser(), true);
    }

    public List<Message> loadMyUnreadMessages() {
        return repository.findByRecipientAndRead(getAuthenticatedUser(), false);
    }

}
