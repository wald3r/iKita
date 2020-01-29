package at.ikita.ui.beans;

import java.io.Serializable;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Set;

import org.primefaces.event.FileUploadEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import at.ikita.model.Child;
import at.ikita.model.Person;
import at.ikita.model.PersonRole;
import at.ikita.model.Picture;
import at.ikita.services.ChildService;
import at.ikita.services.PersonService;
import at.ikita.services.PictureService;
import at.ikita.services.RegistrationInfoService;
import at.ikita.ui.controllers.FileUploadController;

/**
 * Bean to create and edit Persons(Teacher, Guardians and Parents)
 * 
 * @author daniel.walder@student.uibk.ac.at
 * @author Kerstin.Klabischnig@student.uibk.ac.at
 */

@Component
@Scope("view")
public class PersonBean implements Serializable{
	
	@Autowired
	private PersonService personService;
	@Autowired
	private PictureService pictureService;
	@Autowired 
	private FileUploadController fileUpload;
	@Autowired
	private ChildService childService;
	
	@Autowired
	private RegistrationInfoService registrationInfoService;
	
	Person person;	
	
	private String email;
	private String lastName;
	private String firstName;
	private String privateTelephone;
	private String workTelephone;
	private Picture picture;
	private String comment;
	private boolean confirmedGuardian;
	private boolean mailNotification;
	private boolean mailLunchReminder;
	private String[] existing_roles = new String[4];
	private String[] new_roles = new String[4];
 	private Set<PersonRole> roles = new HashSet<>();
	private int status = 9;
	private Set<String> children1 = new HashSet<>();
	private Set<Child> children = new HashSet<>();
	private Set <Person> guardians = new HashSet<>();
	
	
	/**
	 * Reload person from database
	 * 
	 */
	public void doReloadPerson() {
        person = personService.loadEntry(person.getId());
      
    }
	
	/**
	 * 
	 * 
	 * @return currently logged in person
	 */
	public Person getCurrentPerson() {
 		return personService.getAuthenticatedUser();
	}
	
	
	/**
	 * Save Person
	 * 
	 */
    public void doSavePerson() {
        person = this.personService.saveEntry(person);
    }
    
	
 
    /**
     * Resets all variables
     * 
     */
    public void clearSettings(){
    	email = null;
    	lastName = null;
    	firstName = null;
    	privateTelephone = null;
    	workTelephone = null;
    	picture = null;
    	comment = null;
    	mailLunchReminder = false;    	
    	mailNotification = false;
    	new_roles = new String[4];
    	existing_roles = new String[4];
    	roles.clear();
    	status = 9;
    	children.clear();
    	children1.clear();
    	
    }

   
    /**
     * add Roles 
     */
    public void addNewRoles(){
    	if(personService.getAuthenticatedUser().getRoles().contains(PersonRole.PARENT) && !personService.getAuthenticatedUser().getRoles().contains(PersonRole.TEACHER) && !personService.getAuthenticatedUser().getRoles().contains(PersonRole.ADMIN)){
    		roles.add(PersonRole.GUARDIAN);
    		confirmedGuardian = false;
    	}
    	else{
    		for(int i = 0; i < new_roles.length; i++){
    			if(new_roles[i].contains("admin")){roles.add(PersonRole.ADMIN);}
    			else if(new_roles[i].contains("teacher")){roles.add(PersonRole.TEACHER);}
    			else if(new_roles[i].contains("guardian")){roles.add(PersonRole.GUARDIAN);}
    			else{roles.add(PersonRole.PARENT);}
    		}
    		confirmedGuardian = true;
    	}
    }
    
   
    /**
     * Method to set all variables for a new Person and gives a response to the gui
     */
    public void newPerson(){
		addNewRoles();
		
		Person person_check = null;
    
		if(firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || roles.isEmpty()){
	        status = 1;
		}
		else if((person_check = personService.loadByEmail(email)) != null){
	    	status = 2;
	    }
		else{
			person = Person.create(email, lastName, firstName, "passwd", roles);
    		person.setComment(comment);
    		person.setPrivateTelephone(privateTelephone);
    		person.setWorkTelephone(workTelephone);
    		person.setMailLunchReminder(mailLunchReminder);
    		person.setMailNotification(mailNotification);
    		person.setActivated(confirmedGuardian);
    		person.setPicture(picture);
    		
    		doSavePerson();
    		
    		selectedChildrenStore();
    		storeGuardians();
    		if(person.getRoles().contains(PersonRole.ADMIN) || person.getRoles().contains(PersonRole.PARENT)  || person.getRoles().contains(PersonRole.TEACHER)){
    			registrationInfoService.sendAccessData(person);
    		}
    		status = 0;
		}
    }
   
    /**
     * set new guardian to selected children
     */
    public void storeGuardians(){
    	
    	if(!children.isEmpty()){
			for(Child child : children){
				if (!child.getGuardians().isEmpty()){
					guardians = child.getGuardians();
				}
				guardians.add(person);
				child.setGuardians(guardians);
				childService.saveEntry(child);
			}
		}
    }

	/**
     * Edit Person roles  and call storeExistingPerson() function
     * 
     */
    public void editExistingRoles(){
    	if(existing_roles.length > 0){
    		for(String k: existing_roles){
    			if(k.contains("admin")){roles.add(PersonRole.ADMIN);}
    			else if(k.contains("teacher")){roles.add(PersonRole.TEACHER);}
    			else if(k.contains("guardian")){roles.add(PersonRole.GUARDIAN);}
    			else{roles.add(PersonRole.PARENT);}
    		}
    		
    		person.setRoles(roles);
    		if(roles.contains(PersonRole.GUARDIAN) && roles.size() == 1){
    			person.setEnabled(false);
    		}
    		else{
    			person.setEnabled(true);
    		}
    	}
    }
    
    /**
     * Short check if required variables are set and then save person
     * 
     */
    public void storeExistingPerson() {
    	editExistingRoles();
    	if(!person.getFirstName().isEmpty() && !person.getLastName().isEmpty()){
    		if(picture != null){
    			person.setPicture(picture);
    		}
    		doSavePerson();
    		status = 0;
    		clearSettings();
		}
		else{
			status = 1;
		}
    }
        
    
    /**
     * edit Login Details
     * 
     */
    public void storeLoginDetails(){
    	if(!person.getPassword().isEmpty() && !person.getEmail().isEmpty()){
    		doSavePerson();
    		status = 0;
    	}
    	else{
    		status = 1;
    	}
    	
    }
    
    

    /**
     * Confirm Guardian and Save him
     * 
     */
    public void confirmGuardian(){
    	person.setActivated(true);
    	doSavePerson();
    }
    
    
    /**
     * Delete Person
     * 
     */
    public void doDeletePerson() {
        this.personService.deleteEntry(person);
        person = null;
    }

    
    /**
     * Calls the FileUploadController to upload a Picture and creates a new Picture Object to store the path
	 *
     * @param file Picture to upload
     */
    public void uploadPicture(FileUploadEvent file){
    	String pathtoPicture = fileUpload.handleFileUpload(file);
    	picture = Picture.create(pathtoPicture);
    	pictureService.saveEntry(picture);
    	picture = pictureService.loadByURI(pathtoPicture);
    }
    
    /**
     * converts a set of strings into a set of children
     */
	public void selectedChildrenStore(){
		
		
		if(!children1.isEmpty()){
			
    		for(String child1 : children1){
    
    			String name = child1.substring(0, child1.indexOf("(")-1);
    			String lastname1 = name.substring(0, name.indexOf(" "));
    			String firstname1 = name.substring(name.indexOf(" ")+1, name.length());
    			
    			String birthday1 = child1.substring(child1.indexOf("(")+1, child1.indexOf(")"));
    			String day = birthday1.substring(0, 2);
    			int day1 = Integer.parseInt(day);
    			String month = birthday1.substring(3, 5);
    			int month1 = Integer.parseInt(month);
    			String year = birthday1.substring(6, 10);
    			int year1 = Integer.parseInt(year);
    	
    	 		Date birthday = new GregorianCalendar(year1, month1 -1, day1).getTime();
    	 		
    	 		Child store_children = childService.loadByNameAndBirthday(lastname1, firstname1, birthday);
    	 		children.add(store_children);
    		}
    	}
    }

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getPrivateTelephone() {
		return privateTelephone;
	}

	public void setPrivateTelephone(String privateTelephone) {
		this.privateTelephone = privateTelephone;
	}

	public String getWorkTelephone() {
		return workTelephone;
	}

	public void setWorkTelephone(String workTelephone) {
		this.workTelephone = workTelephone;
	}

	public Picture getPicture() {
		return picture;
	}

	public void setPicture(Picture picture) {
		this.picture = picture;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public boolean isMailNotification() {
		return mailNotification;
	}

	public void setMailNotification(boolean mailNotification) {
		this.mailNotification = mailNotification;
	}

	public boolean isMailLunchReminder() {
		return mailLunchReminder;
	}

	public void setMailLunchReminder(boolean mailLunchReminder) {
		this.mailLunchReminder = mailLunchReminder;
	}

	public Set<PersonRole> getRoles() {
		return roles;
	}

	public void setRoles(Set<PersonRole> roles) {
		this.roles = roles;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public Person getPerson() {
		return person;
	}

	public void setPerson(Person person) {
		this.person = person;
		doReloadPerson();
		clearSettings();
	}
	
	public boolean isConfirmedGuardian() {
		return confirmedGuardian;
	}

	public void setConfirmedGuardian(boolean confirmedGuardian) {
		this.confirmedGuardian = confirmedGuardian;
	}

	public String[] getExisting_roles() {
		return existing_roles;
	}

	public Set<String> getChildren1() {
		return children1;
	}

	public void setChildren1(Set<String> children1) {
		this.children1 = children1;
	}

	public Set<Child> getChildren() {
		return children;
	}


	public void setChildren(Set<Child> children) {
		this.children = children;
	}


	public String[] getNew_roles() {
		return new_roles;
	}

	public void setExisting_roles(String[] existing_roles) {
		this.existing_roles = existing_roles;
	}

	public void setNew_roles(String[] new_roles) {
		this.new_roles = new_roles;
	}

    
    public Set<Person> getGuardians() {
		return guardians;
	}

	public void setGuardians(Set<Person> guardians) {
		this.guardians = guardians;
	}

}
