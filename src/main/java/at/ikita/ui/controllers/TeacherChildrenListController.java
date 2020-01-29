package at.ikita.ui.controllers;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;
import at.ikita.model.Child;
import at.ikita.services.ChildService;

/**
 * Controller for a Teacher to list all children
 * 
 * @author daniel.walder@student.uibk.ac.at
 *
 */
@Component
@Scope("view")
public class TeacherChildrenListController  implements Serializable {

	
	/**
	 * 
	 */
	@Autowired
	 private ChildService childService;

	
	
     /**
      * Lists to filter Children	
      */
	 protected List<Child> filteredChild1;
	 protected List<Child> filteredChild2;

	
	 	
	  /**
	   * 
	   * @return a list of all active Children
	   */
	   @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('TEACHER')")
	   public Collection<Child> getActiveChildren(){
		   return childService.loadActive(); 
	   }
	
	   
	   /**
	    * 
	    * @return a list of all inactive Children
	    */
	   @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('TEACHER')")
	   public Collection<Child> getInactiveChildren(){
			  return childService.loadInactive(); 
		   }


	   public List<Child> getFilteredChild1() {
	    	return filteredChild1;
	   }


	  public void setFilteredChild1(List<Child> filteredChild1) {
		    this.filteredChild1 = filteredChild1;
	  }


	   public List<Child> getFilteredChild2() {
	     	return filteredChild2;
	   }


	   public void setFilteredChild2(List<Child> filteredChild2) {
		     this.filteredChild2 = filteredChild2;
	   }

	   
	
}
