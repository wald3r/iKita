package at.ikita.ui.beans;

import java.io.Serializable;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import at.ikita.model.ParentTask;
import at.ikita.model.Person;
import at.ikita.services.ParentTaskService;
import at.ikita.services.PersonService;
import at.ikita.services.TaskNotificationService;


/**
 * 
 * A Bean to create and edit Tasks
 * 
 * @author daniel.walder@student.uibk.ac.at
 *
 */
@Component
@Scope("view")
public class TaskBean implements Serializable{

	
	
	@Autowired
	ParentTaskService parentTaskService;
	
	@Autowired
	PersonService personService;
	
	ParentTask task;

	@Autowired
	TaskNotificationService taskNotificationService;
	
	
	
	private Person person1;
	private String person;
	private Date startDate = new Date();
	private Date endDate = new Date();
	private String description;
	private String title;
	private int status = 9;
	private boolean urgent;
	
	
	
	
	/**
	 * reset all variables 
	 */
	public void clearSettings(){
		person = null;
		person1 = null;
		startDate = null;
		endDate = null;
		description = null;
		status = 9;
		title = null;
		urgent = false;
	}
	
	
	/**
	 * reload task from database
	 */
	public void doReloadTask() {
        task = parentTaskService.loadEntry(task.getId());
    }
	
	/**
	 * save task in database
	 */
    public void doSaveTask() {
        task = this.parentTaskService.saveEntry(task);
    }
    
   
    /**
     * create new Task and give a response to gui
     * 
     */
    public void newTask(){
      
    	if(person.isEmpty() ||  title.isEmpty() || startDate == null || endDate == null ){
    		status = 1;
    	}
    	else if (startDate.after(endDate)){
    		status = 2;

    	}
    	else{
      		task = ParentTask.create();
      		task.setTitle(title);
      		task.setStartDate(startDate);
      		task.setEndDate(endDate);
            task.setDescription(description);
            task.setUrgent(urgent);
        	addPerson();
        	if(task.getPerson().isMailNotification()) {
             	taskNotificationService.sendNotification(task);
           }
            doSaveTask();
       	    status = 0;
      	}
    }
    
    
    /**
     * Edit existing Task and give a response to the gui
     * 
     */
    public void editTask(){
    	addPerson();
    	
    	if(task.getTitle().isEmpty() || task.getStartDate() == null || task.getEndDate() == null || task.getPerson().equals(null)){
    		status = 1;
    	}
    	else if(task.getStartDate().after(task.getEndDate())){
    		status = 2;
    	}
    	else{
    	    doSaveTask();
		    if(task.getPerson().isMailNotification()) {
		    taskNotificationService.sendNotification(task);
		    }
     	    status = 0;
    	}
    }
    
    
    
    /** 
     * delete task
     */
    public void doDeleteTask(){
    	 this.parentTaskService.deleteEntry(task);
         task = null;
    }
    
    
    /**
     * Convert a String to a Person and add him to the task
     * 
     */
    public void addPerson(){

    	if(!person.isEmpty()){
    		String result = person.substring(person.indexOf("<") + 1, person.indexOf(">"));
    		person1 = personService.loadByEmail(result);
    		task.setPerson(person1);
    	}
    }
    
	
    /**
     * reopen task
     * 
     */
    public void openTask(){
    	task.setDone(false);
    	doSaveTask();
    	task = null;
    }
    
    
    /**
     * close task
     * 
     */
    public void finishTask(){
    	task.setDone(true);
		doSaveTask();
		task = null;
    }
	
    
    
	public String getPerson() {
		return person;
	}
	
	public void setPerson(String person) {
		this.person = person;
	}
	
	public Date getStartDate() {
		return startDate;
	}
	
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	
	public Date getEndDate() {
		return endDate;
	}
	
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public String getTitle() {
		return title;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	public boolean isUrgent() {
		return urgent;
	}
	
	public void setUrgent(boolean urgent) {
		this.urgent = urgent;
	}

	public ParentTask getTask() {
		return task;
	}

	public void setTask(ParentTask task) {
		this.task = task;
		clearSettings();
		doReloadTask();
	}
	

	public Person getPerson1() {
		return person1;
	}



	public void setPerson1(Person person1) {
		this.person1 = person1;
	}



	public int getStatus() {
		return status;
	}



	public void setStatus(int status) {
		this.status = status;
	}

	
	
}
