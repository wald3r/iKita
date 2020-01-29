package at.ikita.services;

import java.util.Date;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import at.ikita.model.Child;
import at.ikita.model.ChildCalendar;

/**
 * Service for the teacher calendar
 * 
 * @author Andre.Potocnik@student.uibk.ac.at
 *
 */
@Component
@Scope("request")
public class TeacherCalendarChildService {
    

    /**
     * Check if alternative bring or pickup time is set and return
     * it. Return default bring/pickup time otherwise.
     * 
     * @param child to check times
     * 
     * @param flag determine bring or pickup time (1 = bring, 2 = pickup)
     * 
     * @return Actual bring/pickup time
     */
    public Date getActualBringPickupTime(ChildCalendar childCalendar, int flag) {
        Date bringPickupTime = null;
        
        switch(flag) {
            
            case 1 : 
                if(childCalendar.getBringTime() == null) {
                    bringPickupTime = childCalendar.getChild().getDefaultBringTime();
                } else {
                    bringPickupTime = childCalendar.getBringTime();
                }
                break;
            
            case 2 :
                if(childCalendar.getPickupTime() == null) {
                    bringPickupTime = childCalendar.getChild().getDefaultPickupTime();
                } else {
                    bringPickupTime = childCalendar.getPickupTime();
                }
                break;
        }
        return bringPickupTime;
    }
    
    /**
     * String builder for the event title (name of child)
     * 
     * @param childCalendar information of child
     * 
     * @return string with name of child
     */
    public String childDataToString(ChildCalendar childCalendar) {
        Child child = childCalendar.getChild();
        String firstName = child.getFirstName();
        String lastName = child.getLastName();
        String dataString = " Kind: " + firstName + " " + lastName;
        return dataString;
    }
}
