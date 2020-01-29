package at.ikita.ui.controllers;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import at.ikita.model.ChildCalendar;
import at.ikita.model.Person;
import at.ikita.model.PersonRole;
import at.ikita.model.TeacherCalendar;
import at.ikita.services.ChildCalendarService;
import at.ikita.services.PersonService;
import at.ikita.services.TeacherCalendarService;

/**
 * Controller for manipulating and accessing all
 * necessary teacher calendar month/week/day data
 *  
 * @author Andre.Potocnik@student.uibk.ac.at
 *
 */
@Component
@Scope("view")
public class TeacherCalendarController {
	
	@Autowired
	private ChildCalendarService childCalendarService;
	
	@Autowired
	private TeacherCalendarService teacherCalendarService;
	
	@Autowired
	private PersonService personService;
	
	public int countPresentChildrenByDate(Date date) {
		List<ChildCalendar> childCalendarEntries = childCalendarService.loadByDate(date); 
		return childCalendarEntries.size();
	}
	
	public int countPresentTeacherByDate(Date date) {
		List<TeacherCalendar> teacherCalendarEntries = teacherCalendarService.loadByDate(date);
		return teacherCalendarEntries.size();
	}
	
	public int countChildrenMealsByDate(Date date) {
		List<ChildCalendar> childCalendarEntries = childCalendarService.loadByDate(date);
		int lunch = 0;
		
		for(ChildCalendar i : childCalendarEntries) {
			if(i.hasLunch()) {
				lunch++;
			}
		}
		return lunch;
	}
	
	public int countTeacherMealsByDate(Date date) {
		List<TeacherCalendar> TeacherCalendarEntries = teacherCalendarService.loadByDate(date);
		int lunch = 0;
		
		for(TeacherCalendar i : TeacherCalendarEntries) {
			if(i.hasLunch()) {
				lunch++;
			}
		}
		return lunch;
	}
	
	public List<ChildCalendar> getPresentChildrenByDate(Date date) {
		return childCalendarService.loadByDate(date);
	}
	
	public List<TeacherCalendar> getPresentTeacherByDate(Date date) {
		return teacherCalendarService.loadByDate(date);
	}
	
	public void doSaveTeacherCalendar(TeacherCalendar teacherCalendar) {
	    teacherCalendarService.saveEntry(teacherCalendar);
	}
	
	public Map<String, Person> getAllTeacher() {
	    List<Person> allTeacherList = personService.loadAllByRole(PersonRole.TEACHER);
	    Map<String, Person> allTeacherHashMap = new HashMap<>();
	    
	    for(Person i : allTeacherList) {
	        allTeacherHashMap.put(i.getEmail(), i);
	    }
	    return allTeacherHashMap;
	}
	
 }
