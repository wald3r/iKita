package at.ikita.services;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import at.ikita.model.Child;
import at.ikita.model.Person;
import at.ikita.model.PersonRole;

/**
 * Send a E-Mail notifications to all parents who have activated
 * the lunch reminder notification, to remind them to assign
 * their kids for lunch
 * 
 * @author Andre.Potocnik@student.uibk.ac.at
 *
 */
@Component
@Scope("application")
public class LunchRemainderService {
    
    @Autowired
    private ChildCalendarService childCalendarService;
    
    @Autowired
    private PersonService personService;
    
    @Autowired
    private MailService mailService;
    
    /**
     * Get all parents who have activated the lunch reminder
     * 
     * @return all potential parents
     */
    public List<Person> getReminderCandidates() {
       List<Person> allParents = personService.loadAllByRole(PersonRole.PARENT); 
       List<Person> candidateParents = new ArrayList<>();
       
       for(Person i : allParents) {
           if(i.isMailLunchReminder()) {
               candidateParents.add(i);
           }
       }
       
       return candidateParents;
    }
    
    /**
     * Iterate through all parents who have activated the lunch
     * reminder, check if they have assigned their kids to
     * lunch, if not notify them
     * 
     * @param currentDay when task scheduler is fired (FRIDAY)
     */
    public void checkCandidateChildren(Date currentDay) {
        Set<Child> children = null; 
        List<Person> candidateParents = getReminderCandidates();
        Date start = getStartDate(currentDay); // the beginning of the next week (MONDAY)
        Date end = getEndDate(start); // the end of the next week (FRIDAY)
        Date day = null;
        StringBuilder mailBody = new StringBuilder();
        String mailHeader = null;
        String completeMailText = null;
        String subject = "Erinnerung Essen";
        
        /* Iterate through all parents who have activated lunch reminder */
        for(Person candidateParent : candidateParents) {
            children = candidateParent.getChildren(); // get all children of current parent
            
            /* Iterate through all children of current parent */ 
            for(Child child : children) {
                day = start; // set start date (beginning of the week)
                
                /* Go through whole week */
                while(day.before(end)) {
                    /* If current child is not assigned to lunch on current day,
                     * append it to the E-Mail body String
                     */
                    if((childCalendarService.loadByChildAndDate(child, day) != null) &&
                       (!childCalendarService.loadByChildAndDate(child, day).hasLunch())) {
                        mailBody.append("\t" + day + " ");
                        mailBody.append(child.getFirstName() + " ");
                        mailBody.append(child.getLastName() + "\n");
                    }
                    day = addDay(day);
                }
            }
            
            /* If the E-Mail body String is not empty, than their must be at least one
             * child which is not assigned to lunch and so we have to send a E-Mail to
             * the parent.
             */
            if(mailBody.length() != 0) { 
                mailHeader = mailHeader(candidateParent); 
                completeMailText = completeMailText(mailHeader, mailBody.toString());
                mailService.sendSingleMail(candidateParent.getEmail(), subject, completeMailText);
            }
            mailBody = new StringBuilder();
        }
    }
    
    /**
     * Beginning of the E-Mail
     * 
     * @param parent to retrieve name
     * 
     * @return Personalized E-Mail header String
     */
    public String mailHeader(Person parent) {
        StringBuilder header = new StringBuilder();
        
        header.append("Hallo ");
        header.append(parent.getFirstName() + " ");
        header.append(parent.getLastName() + "!\n\n");
        header.append("Folgende Kinder sind kommende Woche nicht zum essen angemeldet:\n\n");
        
        return header.toString();
    }
    
    /**
     * Generate the complete E-Mail content
     * 
     * @param header personalized E-Mail header
     * @param mailBody personalized E-Mail body
     *
     * @return complete E-Mail text
     */
    public String completeMailText(String header, String mailBody) {
        StringBuilder text = new StringBuilder();
        
        text.append(header);
        text.append(mailBody);
        text.append("\nDein iKita Team");
        
        return text.toString();
    }
    
    /**
     * Get the start date of the beginning of the next
     * week, when the task scheduler executes
     * 
     * @param currentDay when the task scheduler executes
     * @return
     */
    public Date getStartDate(Date currentDay) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentDay);
        calendar.add(Calendar.DATE, 3);
        
        return calendar.getTime();
    }
    
    /**
     * Get the end date of the next week, when
     * the task scheduler executes
     * 
     * @param start beginning of week
     *
     * @return end of week
     */
    public Date getEndDate(Date start) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(start);
        calendar.add(Calendar.DATE, 5);
        
        return calendar.getTime();
    }
    
    /**
     * Add one day (24h) to a DATE
     * 
     * @param date to manipulate
     * 
     * @return manipulated date
     */
    public Date addDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_MONTH, 1);
                
        return calendar.getTime();
    }
}
