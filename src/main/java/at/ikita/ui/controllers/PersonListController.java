package at.ikita.ui.controllers; 
import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import at.ikita.model.Person; 
import at.ikita.model.PersonRole; 
import at.ikita.services.PersonService; 
import org.springframework.beans.factory.annotation.Autowired; 
import org.springframework.context.annotation.Scope;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component; 
import org.springframework.util.Assert; 

/**  
 * Controller for the Person list view 
 * 
 * @author Fabio.Valentini@student.uibk.ac.at 
 * @author daniel.walder@student.uibk.ac.at 
 */ 

@Component 
@Scope("view") 
public class PersonListController  implements Serializable { 
	
	private PersonService personService;      
	
	/**
	 * 
	 * Lists to filter the datatables
	 * 
	 */
    protected List<Person> filteredPersons1;
	protected List<Person> filteredPersons2;
	
	
	@Autowired      
	public PersonListController(PersonService personService) {
		Assert.notNull(personService);         
		this.personService = personService;      
	}       
	
	
	/** 	  
	 *   	   
     * @return  a list of all parents 	   
     */ 	   
	public Collection<Person> getAllParents() { 	      
		return personService.loadAllByRole(PersonRole.PARENT); 	   
	} 	
	
	
	/** 	  
	 * 	    
	 *	@return a list of all guardians 	    	    
	 */ 	
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('TEACHER')")
	public Collection<Person> getAllGuardians(){ 		      
		 return personService.loadAllByRole(PersonRole.GUARDIAN);
	} 	   
	
   
    /**
     * 
     * @return a list of all admins
     */
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('TEACHER')")
    public Collection<Person> getAllAdmins(){
    	return personService.loadAllByRole(PersonRole.ADMIN);
    }
    
	
	/**
	 * 
	 * @return a list of all persons
	 */
	public Collection<Person> getAllPersons(){ 		
		return personService.loadAll(); 	  
	}
	
	/**
	 * 
	 * @return a list of all teachers
	 */
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('TEACHER')")
	public Collection<Person> getAllTeacher(){ 		
    	
		return personService.loadAllByRole(PersonRole.TEACHER);
	} 	   	  
    
   /**
    * 
    * @return a set of active parens
    */
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('TEACHER')")
    public Collection<Person> getActiveParents(){
    	return personService.loadAllActiveParents();
    }
    
   /**
    * 
    * @return a set of inactive parents
    */
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('TEACHER')")
    public Collection<Person> getInactiveParents(){
    	return personService.loadAllInActiveParents();
    }
	
	/**
	 * 
	 * 
	 * @return a list of all confirmed guardians
	 */
	public Collection<Person> getAllConfirmedGuardians(){ 	
		return personService.loadAllConfirmedGuardians(); 
		}
	
	/**
	 * 
	 * 
	 * @return a list of all unconfirmed guardians
	 */
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('TEACHER')")
	public Collection<Person> getAllUnconfirmedGuardians(){
		return personService.loadAllUnconfirmedGuardians();
	}
    
    /**
     * short check if current parent is active or not
     * 
     * @return boolean
     */
    public Boolean isParentActive(){
    	Person parent = personService.getAuthenticatedUser();
    	return parent.isActive();
    }
    
    
    public List<Person> getFilteredPersons1() {
		return filteredPersons1;
	}

	public void setFilteredPersons1(List<Person> filteredPersons1) {
		this.filteredPersons1 = filteredPersons1;
	}


	public List<Person> getFilteredPersons2() {
		return filteredPersons2;
	}


	public void setFilteredPersons2(List<Person> filteredPersons2) {
		this.filteredPersons2 = filteredPersons2;
	}
    
}
 