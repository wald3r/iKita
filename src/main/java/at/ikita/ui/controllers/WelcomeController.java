package at.ikita.ui.controllers;


import java.io.Serializable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import at.ikita.services.PersonService;

/**
 * 
 * A controller to count all unconfirmed guardians for the welcome.xhtml
 * 
 * @author daniel.walder@student.uibk.ac.at
 *
 */
@Component
@Scope("view")
public class WelcomeController implements Serializable {

	@Autowired
	PersonService personService;
	
	 	
	
	/**
	 * 
	 * 
	 * @return size of all unconfirmed guardians
	 */
	 public int getAllUnconfirmedGuardians(){
		   return personService.loadAllUnconfirmedGuardians().size();
	   }
	 
	 
	 
	
}
