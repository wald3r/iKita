package at.ikita.services;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import at.ikita.model.ParentTask;

/**
 * Service to notify a person via E-Mail
 * if a task is assigned to this person
 * 
 * @author Andre.Potocnik@student.uibk.ac.at
 *
 */
@Component
@Scope("request")
public class TaskNotificationService {
    
    @Autowired
    private MailService mailService;
    
    /**
     * This method prepares and sends the mail
     * to the person
     * 
     * @param task  
     * 
     */
    public void sendNotification(ParentTask task) {
        String recipient = task.getPerson().getEmail();
        String subject = "Dir wurde eine neue Aufgabe zugeteilt!";
        String message = createMailText(task);
        
        mailService.sendSingleMail(recipient, subject, message);
    }
    
    /**
     * This method generates the main E-Mail text
     * 
     * @param task to retrieve informations
     * 
     * @return generated mail text
     */
    public String createMailText(ParentTask task) {
        StringBuilder message = new StringBuilder();
   
        message.append("Hallo ");
        message.append(task.getPerson().getFirstName() + " ");
        message.append(task.getPerson().getLastName() + "!\n\n");
        message.append("Dir wurde folgende Aufgabe zugeteilt:\n");
        message.append("\n\t");
        message.append(task.getTitle() + "\n\n");
        message.append("Diese Aufgabe ist ");
        
        if(task.isUrgent()) {
            message.append("DRINGEND ");
        }
        
        message.append("bis zum ");
        message.append(task.getEndDate());
        message.append(" zu erledigen!\n");
        
        if(task.getDescription() != null) {
            message.append("\nBeschreibung:");
            message.append("\n\n\t");
            message.append(task.getDescription() + "\n");
        }
        
        message.append("\nDanke!\n");
        message.append("Dein iKita Team");
        
        return message.toString();
    }
}
