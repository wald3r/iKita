package at.ikita.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import at.ikita.model.Person;

/**
 * Service that sends access data if person
 * registration is completed
 * 
 * @author Andre.Potocnik@student.uibk.ac.at
 *
 */
@Component
@Scope("request")
public class RegistrationInfoService {
    
    @Autowired
    private MailService mailService;
    
    /**
     * This method prepares and sends the mail
     * to the person
     * 
     * @param person
     */
    public void sendAccessData(Person person) {
        String recipient = person.getEmail();
        String subject = "Deine Zugangsdaten";
        String message = createMailText(person);
        
        mailService.sendSingleMail(recipient, subject, message);
    }
    
    /**
     * This method generates the main E-Mail text
     * 
     * @param person to retrieve informations
     * 
     * @return generated mail text
     */
    public String createMailText(Person person) {
        StringBuilder message = new StringBuilder();
        
        message.append("Hallo ");
        message.append(person.getFirstName() + " ");
        message.append(person.getLastName() + "!\n\n");
        message.append("Dein Zugang für iKita wurde freigeschaltet!\n");
        message.append("Du kannst dich nun mit deiner E-Mail-Adresse und deinem Passwort anmelden.\n\n");
        message.append("\tPasswort: ");
        message.append("passwd");
        message.append("\n\nDu solltest dein Passwort umgehend ändern!\n\n");
        message.append("Dein iKita Team");
        
        return message.toString();
    }
    
}
