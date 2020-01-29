package at.ikita.configs;

import java.util.Properties;

import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.context.annotation.Bean;

/**
 * Configuration for the Mail-Service 
 * 
 * @author Andre.Potocnik@student.uibk.ac.at
 *
 */
@Configuration
public class MailConfiguration {
    
    /**
     * Set mail host login details
     * 
     * @return a callable javaMailSender Bean to send e-mails etc.
     */
    @Bean
    public JavaMailSender javaMailService() {
        JavaMailSenderImpl javaMailSender = new JavaMailSenderImpl();
        
        javaMailSender.setHost("smtp.gmail.com");
        javaMailSender.setPort(587);
        javaMailSender.setUsername("");
        javaMailSender.setPassword("");
        javaMailSender.setJavaMailProperties(getMailProperties());
        
        return javaMailSender;
    }
    
    /**
     * Provider specific connection properties
     * 
     * @return provider properties
     */
    private Properties getMailProperties() {
        Properties properties = new Properties();
        properties.setProperty("mail.transport.protocol", "smtp");
        properties.setProperty("mail.smtps.auth", "true");
        properties.setProperty("mail.smtp.starttls.required", "true");
        properties.setProperty("mail.smpt.starttls.enable", "true");
        properties.setProperty("mail.debug", "false"); // set to true for debugging information
            
        return properties;
    }
    
}
