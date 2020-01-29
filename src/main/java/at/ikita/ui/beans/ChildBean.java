package at.ikita.ui.beans;


import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.FlowEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import at.ikita.model.Child;
import at.ikita.model.DayOfWeek;
import at.ikita.model.Person;
import at.ikita.model.Picture;
import at.ikita.model.Gender;
import at.ikita.services.ChildService;
import at.ikita.services.PersonService;
import at.ikita.services.PictureService;
import at.ikita.ui.controllers.FileUploadController;
/**
 * 
 * Bean to create and edit Child
 * 
 * @author daniel.walder@student.uibk.ac.at
 *
 */
@Component
@Scope("view")
public class ChildBean implements Serializable {


    @Autowired
    private ChildService childService;

    @Autowired
    private PersonService personService;
    
    @Autowired
    private PictureService pictureService;

    @Autowired
    FileUploadController fileUpload;
    
    Child child;

   
    private String lastName;
    private String firstName;
    private Gender enum_gender;
    private Date birthday;
    private Date registrationDate;
    private Date deRegistrationDate;
    private Picture new_picture;
    private String comment;
    private Set<Person> guardian = new HashSet<>();
    private Person emergencyContact;
    private String emergencyContact1;
    private Set<String> guardian1 = new HashSet<>();
    private String allergies;
    private Set<String> selectedParents = new HashSet<>();
    private Set<Person> storedParents = new HashSet<>();
    private Set<Child> storedSiblings = new HashSet<>();
    private Set<DayOfWeek> defaultPresences = new HashSet<>();
    private String[] defaultPresences1 = new String[7];
    private Date defaultBringTime;
    private Date defaultPickupTime;
    private int status = 9;
    private String sex;
    private boolean faces_skip;


    /**
     * reload child from database
     */
    public void doReloadChild() {
        child = childService.loadByNameAndBirthday(child.getLastName(), child.getFirstName(), child.getBirthday());

    }
    
    
    /**
     * 
     * Delete Child from database
     */
    public void doDeleteChild() {
        this.childService.deleteEntry(child);
        child = null;
    }

    /**
     * save child in database
     */
    public void doSaveChild() {
        child = this.childService.saveEntry(child);
    }

    
    
    /**
     * set all variables to save child and give response to gui
     */
    public void newChild() {
        addInformations();
        if (firstName.isEmpty() || lastName.isEmpty() || birthday == null || registrationDate == null || deRegistrationDate == null || selectedParents.size() == 0) {
            status = 1;
        } else if ( registrationDate.after(deRegistrationDate) ||  registrationDate.before(birthday)) {
            status = 2;     
        }else if(selectedParents.size() > 2 ){
        	status = 3;
        } else if ((childService.loadByNameAndBirthday(lastName, firstName, birthday)) != null) {
            status = 4;
        }else{
        	 child = Child.create(lastName, firstName, birthday);
             child.setComment(comment);
             child.setRegistrationDate(registrationDate);
             child.setDeRegistrationDate(deRegistrationDate);
             child.setAllergies(allergies);
             child.setGender(enum_gender);
             child.setDefaultPresences(defaultPresences);
             child.setDefaultBringTime(defaultBringTime);
             child.setDefaultPickupTime(defaultPickupTime);
             child.setEmergencyContact(emergencyContact);
             child.setGuardians(guardian);
             child.setParents(storedParents);
             child.setSiblings(storedSiblings);
             child.setPicture(new_picture);
             doSaveChild();
             status = 0;
        }

    }

    
    /**
     * Short Check if all required variables are set and then the function will call the doSaveChild() Method
     * 
     */
    public void editExistingChild() {
    	addInformations();
    	if(child.getFirstName().isEmpty() || child.getLastName().isEmpty() || child.getBirthday() == null || child.getRegistrationDate() == null || child.getDeRegistrationDate() == null){
    		status = 1;
    	}
    	else if(child.getRegistrationDate().after(child.getDeRegistrationDate()) || child.getRegistrationDate().before(child.getBirthday())){
        	status = 2;
    	}else{
    		if(!storedParents.isEmpty()){child.setParents(storedParents);}
    		if(!storedSiblings.isEmpty()){child.setSiblings(storedSiblings);}
    		if(!guardian.isEmpty()){child.setGuardians(guardian);}
    		if(!defaultPresences.isEmpty()){child.setDefaultPresences(defaultPresences);}
        	if(emergencyContact != null){child.setEmergencyContact(emergencyContact);}
            if(new_picture != null){child.setPicture(new_picture);}
            if(enum_gender != null){child.setGender(enum_gender);}
    		doSaveChild();
    		status = 0;
    	}
    }

    
    /**
     * Method which calls other Methods to add Informations for the child variables
     * 
     */
    public void addInformations() {
    	addDefaultPresences();
        addGender();
        addEmergencyContact();
        selectedParentsStore();
        addGuardian();
        storeSiblings();

    }

    /**
     * Add all the days where the child is present
     * 
     */
    public void addDefaultPresences(){
    	if(defaultPresences1.length > 0){
    		for (int i = 0; i < defaultPresences1.length; i++) {
	             if (defaultPresences1[i].contains("mon")) {
	                 defaultPresences.add(DayOfWeek.MONDAY);
	             } else if (defaultPresences1[i].contains("tue")) {
	                 defaultPresences.add(DayOfWeek.TUESDAY);
	             } else if (defaultPresences1[i].contains("wed")) {
	                 defaultPresences.add(DayOfWeek.WEDNESDAY);
	             } else if (defaultPresences1[i].contains("thurs")) {
	                 defaultPresences.add(DayOfWeek.THURSDAY);
	             } else if (defaultPresences1[i].contains("fri")) {
	                 defaultPresences.add(DayOfWeek.FRIDAY);
	             } else if (defaultPresences1[i].contains("sat")) {
            	 defaultPresences.add(DayOfWeek.SATURDAY);
             	} else {
            	 	defaultPresences.add(DayOfWeek.SUNDAY);
             	}
         	}
    	}
    	
    }
    
    /**
     * set gender of child
     */
    public void addGender(){
    	if(!sex.isEmpty()){
    		if(sex.contentEquals("male")) {
    			enum_gender = Gender.MALE;
    		} 
    		else if (sex.contentEquals("female")) {
    			enum_gender = Gender.FEMALE;
    		} 
    		else if(sex.contentEquals("other")){
    			enum_gender = Gender.OTHER;
    		}
    	}
    }
    
    
    /**
     * Converts Strings to Person(parent) Objects
     */
    public void selectedParentsStore() {
    	if(!selectedParents.isEmpty()){
    		for (String parent : selectedParents) {
    			String result = parent.substring(parent.indexOf("<") + 1, parent.indexOf(">"));
    			Person store_parent = personService.loadByEmail(result);
    			storedParents.add(store_parent);
    		}
    	}
    }
    
    /**
     * gets all Children from the Parents and stores them 
     * 
     */
    public void storeSiblings(){
 
    	if(!storedParents.isEmpty()){
    		for(Person person: storedParents){
    			storedSiblings = person.getChildren();
    		}
    	
    		
    	}
    }
    
    
    
    
    /**
     * Converts Strings to Person(guardian) Objects
     */
    public void addGuardian() {
    	if(!guardian1.isEmpty()){
    		for (String guardians : guardian1) {
    			String result = guardians.substring(guardians.indexOf("<") + 1, guardians.indexOf(">"));
    			Person store_guardian = personService.loadByEmail(result);
    			guardian.add(store_guardian);
    		}
    		this.guardian1.clear();
    	}
    }

    /**
     * Converts a String to a Person(Emergency Contact) Object
     * 
     */
    public void addEmergencyContact() {
        if (!emergencyContact1.isEmpty()) {
            String result = emergencyContact1.substring(emergencyContact1.indexOf("<") + 1, emergencyContact1.indexOf(">"));
            Person store_emergency = personService.loadByEmail(result);
            emergencyContact = store_emergency;
            this.setEmergencyContact1(null);
        }
    }
    
    
    /**
     * Calls the FileUploadController to upload a Picture and creates a new Picture Object to store the path
     * 
     * @param file file to upload
     */
    public void uploadPicture(FileUploadEvent file){
    	String pathtoPicture = fileUpload.handleFileUpload(file);
    	new_picture = Picture.create(pathtoPicture);
    	pictureService.saveEntry(new_picture);
    	new_picture = pictureService.loadByURI(pathtoPicture);
    }
    
    /**
     * resets all variables
     */
    public void clearSettings() {
        this.setBirthday(null);
        this.setComment(null);
        this.setDeRegistrationDate(null);
        this.setRegistrationDate(null);
        this.setLastName(null);
        this.setFirstName(null);
        this.setSex(null);
        this.setNew_picture(null);
        this.setAllergies(null);
        this.setDefaultBringTime(null);
        this.setDefaultPickupTime(null);
        this.setEmergencyContact1(null);
        this.setEmergencyContact(null);
        defaultPresences1 = new String[7];
        guardian.clear();
        selectedParents.clear();
        storedParents.clear();
        storedSiblings.clear();
        defaultPresences.clear();
        status = 9;
    }

    /**      
     *  Wizard to create a new Child step by step      
     *        
     * @param event      
     * @return      
     */     
    public String onFlowProcess(FlowEvent event) {
    	if (faces_skip) {
    		faces_skip = false;
    		return "confirm"; 
    	}
    	else {             
    			return event.getNewStep();        
    		}     
    	}
     

    public Child getChild() {
        return child;
    }

    public void setChild(Child child) {
        this.child = child;
        doReloadChild();
        clearSettings();
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

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public Date getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(Date registrationDate) {
        this.registrationDate = registrationDate;
    }

    public Date getDeRegistrationDate() {
        return deRegistrationDate;
    }

    public void setDeRegistrationDate(Date deRegistrationDate) {
        this.deRegistrationDate = deRegistrationDate;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Person getEmergencyContact() {
        return emergencyContact;
    }

    public void setEmergencyContact(Person emergencyContact) {
        this.emergencyContact = emergencyContact;
    }

    public String getAllergies() {
        return allergies;
    }

    public void setAllergies(String allergies) {
        this.allergies = allergies;
    }

    public Set<String> getSelectedParents() {
        return selectedParents;
    }

    public void setSelectedParents(Set<String> selectedParents) {
        this.selectedParents = selectedParents;
    }

    public String getEmergencyContact1() {
        return emergencyContact1;
    }


    public void setEmergencyContact1(String emergencyContact1) {
        this.emergencyContact1 = emergencyContact1;
    }


    public Set<Person> getStoredParents() {
        return storedParents;
    }

    public void setStoredParents(Set<Person> storedParents) {
        this.storedParents = storedParents;
    }

    public Set<Child> getStoredSiblings() {

        return storedSiblings;
    }


    public void setStoredSiblings(Set<Child> storedSiblings) {
        this.storedSiblings = storedSiblings;
    }


    public int getStatus() {
        return status;
    }


    public void setStatus(int status) {
        this.status = status;
    }

    public Set<DayOfWeek> getDefaultPresences() {
        return defaultPresences;
    }

    public void setDefaultPresences(Set<DayOfWeek> defaultPresences) {
        this.defaultPresences = defaultPresences;
    }


    public Date getDefaultBringTime() {
        return defaultBringTime;
    }

    public void setDefaultBringTime(Date defaultBringTime) {
        this.defaultBringTime = defaultBringTime;
    }

    public Date getDefaultPickupTime() {
        return defaultPickupTime;
    }

    public void setDefaultPickupTime(Date defaultPickupTime) {
        this.defaultPickupTime = defaultPickupTime;
    }

    public Gender getEnum_gender() {
        return enum_gender;
    }


    public void setEnum_gender(Gender enum_gender) {
        this.enum_gender = enum_gender;
    }


    public String getSex() {
        return sex;
    }


    public void setSex(String sex) {
        this.sex = sex;
    }


    public String[] getDefaultPresences1() {
        return defaultPresences1;
    }


    public void setDefaultPresences1(String[] defaultPresences1) {
        this.defaultPresences1 = defaultPresences1;
    }


    public Set<Person> getGuardian() {
        return guardian;
    }


    public void setGuardian(Set<Person> guardian) {
        this.guardian = guardian;
    }


    public Set<String> getGuardian1() {
        return guardian1;
    }


    public void setGuardian1(Set<String> guardian1) {
        this.guardian1 = guardian1;
    }

	public Picture getNew_picture() {
		return new_picture;
	}

	public void setNew_picture(Picture new_picture) {
		this.new_picture = new_picture;
	}


	public boolean isFaces_skip() {
		return faces_skip;
	}


	public void setFaces_skip(boolean faces_skip) {
		this.faces_skip = faces_skip;
	}

    

}
