package at.ikita.ui.controllers; 
import java.io.Serializable;
import java.util.Collection; 
import org.springframework.beans.factory.annotation.Autowired; 
import org.springframework.context.annotation.Scope; 
import org.springframework.stereotype.Component; 

import at.ikita.model.Child; 
import at.ikita.model.Person;
import at.ikita.services.PersonService; 



/**
 * 
 * Controller for Parent View
 * 
 * @author daniel.walder@student.uibk.ac.at
 * @author kerstin.klabischnigg@student.uibk.ac.at
 */
@Component @Scope("view") 
public class ParentChildrenListController implements Serializable { 
	
	@Autowired 	
	private PersonService personService; 
	
	private Person parent;
	
	
	/**
	 * 
	 * @return a set of children from the currently logged in user
	 */
	public Collection<Child> getMyChildren() { 	
		parent = personService.getAuthenticatedUser();
		return parent.getChildren();
	} 
	
	
	
}

