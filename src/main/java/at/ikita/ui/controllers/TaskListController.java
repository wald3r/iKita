package at.ikita.ui.controllers;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;
import at.ikita.model.ParentTask;
import at.ikita.services.ParentTaskService;
import at.ikita.services.PersonService;



/**
 * 
 * Controller to get all Tasks which are done or not done
 * 
 * @author daniel.walder@student.uibk.ac.at
 *
 */
@Component
@Scope("view")
public class TaskListController implements Serializable{

	
	@Autowired
	ParentTaskService parentTaskService;
	
	@Autowired 
	PersonService personService;
	
	/**
	 * Lists to filter Tasks
	 */
    protected List<ParentTask> filteredTasks1;
	protected List<ParentTask> filteredTasks2;
		
		

		
	/**
	 * 
	 * @return a list of Tasks which are still to do
	 */
     @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('TEACHER')")
	 public Collection<ParentTask> getAllUndoneTasks() {
	      return parentTaskService.loadAllTodo();
	   }
	
	 
	 /**
	  * 
	  * @return a list of Tasks which are done
	  */
	 @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('TEACHER')")
	 public Collection<ParentTask> getAllDoneTasks() {
	      return parentTaskService.loadAllDone();
	   }
	
	
	 /**
	  * 
	  * 
	  * @return a list of Tasks for which are done for the parents
	  */
	 public Collection<ParentTask> getParentDoneTasks(){
		 return parentTaskService.loadByPersonAndStatus(personService.getAuthenticatedUser(), true);
	 }
	 
	 
	 /**
	  * 
	  * 
	  * @return a list of Tasks which are not done for the parents
	  */
	 public Collection<ParentTask> getParentUndoneTasks(){
		 return parentTaskService.loadByPersonAndStatus(personService.getAuthenticatedUser(), false);
	 }

	 
	 
	 
	 
	public List<ParentTask> getFilteredTasks1() {
			return filteredTasks1;
	}

	public void setFilteredTasks1 (List<ParentTask> filteredTasks1) {
		this.filteredTasks1 = filteredTasks1;
	}


	public List<ParentTask> getFilteredTasks2() {
		return filteredTasks2;
	}


	public void setFilteredTasks2(List<ParentTask> filteredTasks2) {
		this.filteredTasks2 = filteredTasks2;
	}
}
