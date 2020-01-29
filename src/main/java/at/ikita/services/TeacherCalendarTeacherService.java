package at.ikita.services;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import at.ikita.model.Person;
import at.ikita.model.TeacherCalendar;
import at.ikita.ui.controllers.TeacherCalendarController;


/**
 * Service for teacher calendar 
 * 
 * @author Andre.Potocnik@student.uibk.ac.at
 *
 */

@Component
@Scope("request")
public class TeacherCalendarTeacherService {
    
    @Autowired
    private TeacherCalendarController teacherCalendarController;
    
    private Map<String, Person> teacherObjects;
        
    
    /**
     * Remove all present teacher on a teacher calendar day, from 
     * all teacher set.
     * 
     * @param presentTeacher which are present on a day
     * 
     * @return set with teacher which are not present on a day
     */
    public Map<String, Person> removePresentTeacher(List<TeacherCalendar> presentTeacher) {
        teacherObjects = teacherCalendarController.getAllTeacher();
        
        if(presentTeacher == null) {
            return teacherObjects;
        }
        
        for(TeacherCalendar i : presentTeacher) {
            if(teacherObjects.containsKey(i.getTeacher().getEmail())) {
                teacherObjects.remove(i.getTeacher().getEmail());
            }
        }
        
        return teacherObjects;
    }
    
    /**
     * String builder for the event title (name of teacher)
     * 
     * @param teacherCalendar information of teacher
     * 
     * @return string with name of teacher
     */
    public String teacherDataToString(TeacherCalendar teacherCalendar) {
        Person teacher = teacherCalendar.getTeacher();
        String firstName = teacher.getFirstName();
        String lastName = teacher.getLastName();
        String dataString = " Pädagoge: " + firstName + " " + lastName;
        return dataString;
    }
}
