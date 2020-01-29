package at.ikita.ui.controllers;

import java.util.Collection;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import at.ikita.model.Person;
import at.ikita.model.PersonRole;
import at.ikita.services.PersonService;

/**
 * Controller to get information about guardians
 * 
 * @author Kerstin.Klabischnig@student.uibk.ac.at
 *
 */
@Component
@Scope("view")
public class GuardianListController {

	@Autowired
	private PersonService personService;
	
	/**
	 * 
	 * @return set of guardians from current user (parent)
	 */
	public Set<Person> getMyGuardians(){
	 	return personService.getAuthenticatedUser().getAllAssociatedGuardians();
	}
	
	/**
	 * 
	 * 
	 * @return set of all guardians
	 */
	public Collection<Person> getAllGuardians(){
		return personService.loadAllByRole(PersonRole.GUARDIAN);
	}   
 
}