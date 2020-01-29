package at.ikita.ui.controllers;

import java.io.Serializable;
import java.util.List;

import at.ikita.model.LogMessageType;
import at.ikita.model.Message;
import at.ikita.model.Person;
import at.ikita.model.PersonRole;
import at.ikita.services.LogService;
import at.ikita.services.MessageService;

import at.ikita.services.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;


/**
 * Controller for the Messaging System
 *
 * @author Fabio.Valentini@student.uibk.ac.at
 */

@Component
@Scope("session")
public class MessageController implements Serializable {

    private MessageService messageService;
    private PersonService personService;
    private LogService logService;

    private List<Message> unreadMessageList;
    private List<Message> readMessageList;
    private List<Message> broadcastList;

    private Message original;
    private String newSubject;
    private String newContent;
    private String newRecipient;


    @Autowired
    public MessageController(MessageService messageService,
                             PersonService personService,
                             LogService logService) {
        Assert.notNull(messageService);
        Assert.notNull(personService);
        Assert.notNull(logService);

        this.messageService = messageService;
        this.personService = personService;
        this.logService = logService;

        refresh();
        reset();
    }


    public void refresh() {
        unreadMessageList = messageService.loadMyUnreadMessages();
        readMessageList = messageService.loadMyReadMessages();
        broadcastList = messageService.loadBroadcasts();
    }

    public void reset() {
        original = null;
        newSubject = "";
        newContent = "";
        newRecipient = null;
    }

    public void sendResponse() {
        messageService.sendResponse(original, newContent);
        logService.log(LogMessageType.INFORMATION, this.getClass(),
                String.format("Action: Message response sent (by %s)", personService.getAuthenticatedUser().toString()));
        refresh();
        reset();
    }

    public void sendMessage() {
        if (newRecipient == null)
            throw new NullPointerException();

        messageService.sendMessage(
                personService.loadFromString(newRecipient), newSubject, newContent);
        logService.log(LogMessageType.INFORMATION, this.getClass(),
                String.format("Action: Message sent (by %s)", personService.getAuthenticatedUser().toString()));
        refresh();
        reset();
    }

    public void sendBroadcast() {
        messageService.sendBroadcast(newSubject, newContent);
        logService.log(LogMessageType.INFORMATION, this.getClass(),
                String.format("Action: Broadcast sent (by %s)", personService.getAuthenticatedUser().toString()));
        refresh();
        reset();
    }

    public void doMarkAsRead(Message message) {
        message.setRead(true);
        messageService.saveEntry(message);
        refresh();
    }

    public void doMarkAsUnread(Message message) {
        message.setRead(false);
        messageService.saveEntry(message);
        refresh();
    }

    public void deleteMessage(Message message) {
        messageService.deleteEntry(message);
        refresh();
    }

    public boolean canHideBroadcast(Message message) {
        return message.getSender().equals(messageService.getAuthenticatedUser()) ||
                messageService.getAuthenticatedUser().getRoles().contains(PersonRole.ADMIN);
    }

    public List<Message> getReadMessageList() {
        return readMessageList;
    }

    public List<Message> getUnreadMessageList() {
        return unreadMessageList;
    }

    public List<Message> getBroadcastList() {
        return broadcastList;
    }

    public int getUnreadMessages() {
        return messageService.loadMyUnreadMessages().size();
    }

    public Message getOriginal() {
        return original;
    }

    public void setOriginal(Message original) {
        this.original = original;
    }

    public String getNewSubject() {
        return newSubject;
    }

    public void setNewSubject(String newSubject) {
        this.newSubject = newSubject;
    }

    public String getNewContent() {
        return newContent;
    }

    public void setNewContent(String newContent) {
        this.newContent = newContent;
    }

    public String getNewRecipient() {
        return newRecipient;
    }

    public void setNewRecipient(String newRecipient) {
        this.newRecipient = newRecipient;
    }

    public void setNewRecipientAsPerson(Person newRecipient) {
        this.newRecipient = newRecipient.toString();
    }

}
