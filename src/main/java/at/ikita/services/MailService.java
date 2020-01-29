package at.ikita.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

/**
 * Service to send E-Mails
 * 
 * @author Andre.Potocnik@student.uibk.ac.at
 *
 */
@Component
@Scope("application")
public class MailService {
    
    @Autowired
    private JavaMailSender javaMailSender;
    
    /**
     * Method to send a E-Mail to one receiver
     * 
     * @param from sender
     * @param to recipient
     * @param subject of Mail
     * @param text Mail message
     */
    public void sendSingleMail(String to, String subject, String text) {
        SimpleMailMessage msg = new SimpleMailMessage();
        String from = "team@ikita.at";
        
        msg.setFrom(from);
        msg.setTo(to);
        msg.setSubject(subject);
        msg.setText(text);
        
        /* Simulate email, print content to command line */
        System.out.println("\n+++ EMAIL +++");
        System.out.println("From: " + from);
        System.out.println("To: " + to);
        System.out.println("Subject: " + subject);
        System.out.println(text);
        System.out.println("---------------------\n");
        // TODO activate this line to send emails
        //this.javaMailSender.send(msg);        
    }
}
