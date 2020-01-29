package at.ikita.model;

import java.util.*;

import javax.persistence.*;

import at.ikita.helper.DayOfWeekHelper;
import at.ikita.helper.GenderHelper;

import org.springframework.data.domain.Persistable;
import org.springframework.util.Assert;


/**
 * Entity representing a Child
 *
 * @author Lucas
 * @author Fabio.Valentini@student.uibk.ac.at
 */

@Entity
public class Child implements Persistable<Integer> {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @Column(length = 80, nullable = false)
    private String lastName;

    @Column(length = 80, nullable = false)
    private String firstName;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Column(nullable = false)
    @Temporal(TemporalType.DATE)
    private Date birthday;

    @ManyToMany(targetEntity = Person.class, mappedBy = "children", fetch = FetchType.EAGER,
            cascade = {CascadeType.MERGE, CascadeType.REFRESH})
    private Set<Person> parents;

    @ManyToMany(targetEntity = Person.class, fetch = FetchType.EAGER,
            cascade = {CascadeType.MERGE, CascadeType.REFRESH})
    @JoinTable(name = "CHILD_GUARDIANS",
            joinColumns = @JoinColumn(name = "CHILD_ID", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "GUARDIAN_ID", referencedColumnName = "id"))
    private Set<Person> guardians;

    @ManyToMany(targetEntity = Child.class, fetch = FetchType.EAGER,
            cascade = {CascadeType.MERGE, CascadeType.REFRESH})
    @JoinTable(name = "SIBLINGS",
            joinColumns = @JoinColumn(name = "CHILD1_ID", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "CHILD2_ID", referencedColumnName = "id"))
    private Set<Child> siblings;

    @OneToMany(targetEntity = ChildCalendar.class, mappedBy = "child", orphanRemoval = true,
            cascade = {CascadeType.MERGE, CascadeType.REFRESH, CascadeType.REMOVE})
    private Set<ChildCalendar> calendarEntries;

    @ManyToOne(targetEntity = Picture.class, fetch = FetchType.EAGER,
            cascade = {CascadeType.MERGE, CascadeType.REFRESH, CascadeType.REMOVE})
    private Picture picture;

    @Column(length = 140)
    private String comment;

    @ElementCollection(targetClass = DayOfWeek.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "Child_Days_Of_Week")
    @Enumerated(EnumType.STRING)
    private Set<DayOfWeek> defaultPresences;

    @Temporal(TemporalType.TIME)
    private Date defaultBringTime;

    @Temporal(TemporalType.TIME)
    private Date defaultPickupTime;

    @Column(nullable = false)
    @Temporal(TemporalType.DATE)
    private Date registrationDate;

    @Column(nullable = false)
    @Temporal(TemporalType.DATE)
    private Date deRegistrationDate;

    @Column(length = 511)
    private String allergies;

    @ManyToOne(targetEntity = Person.class, fetch = FetchType.EAGER,
            cascade = {CascadeType.MERGE, CascadeType.REFRESH})
    private Person emergencyContact;

    // Make sure to set newness to true in all constructors.
    @Transient
    private boolean newness = false;


    public static Child create(String lastName, String firstName, Date birthday) {
        Child child = new Child();

        child.lastName = lastName;
        child.firstName = firstName;
        child.birthday = birthday;

        child.parents = new HashSet<>();
        child.guardians = new HashSet<>();
        child.siblings = new HashSet<>();
        child.calendarEntries = new HashSet<>();

        child.newness = true;
        return child;
    }


    // Only Getters and Setters below this point, nothing to see here.
    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public boolean isNew() {
        return newness;
    }

    @Override
    public String toString() {
        return String.format("[Child]: %s %s, (%s)", lastName, firstName, birthday.toString());
    }

    @Override
    public boolean equals(Object o) {
        if (o == null)
            return false;

        if (!(o instanceof Child))
            return false;

        final Child entry = (Child) o;

        // check if IDs are the same
        if ((id != 0) && (entry.getId() != 0)) {
            return (id == entry.getId());
        }

        // otherwise, identifying attributes must be identical
        return (this.firstName.equals(entry.firstName) &&
                this.lastName.equals(entry.lastName) &&
                this.birthday.equals(entry.birthday));
    }

    @Override
    public int hashCode() {
        return Objects.hash(firstName, lastName, gender, birthday);
    }

    public static List<String> getChildStringList(Collection<Child> children) {
        List<String> stringList = new ArrayList<>(children.size());

        for (Child child : children) {
            String name = child.getFirstName();
            name += " ";
            name += child.getLastName();
            stringList.add(name);
        }

        return stringList;
    }

    public String getLastName() {
        return lastName;
    }

    @Deprecated
    public void setLastName(String lastName) {
        Assert.notNull(lastName);
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    @Deprecated
    public void setFirstName(String firstName) {
        Assert.notNull(firstName);
        this.firstName = firstName;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public String getGenderString() {
        if(getGender() != null){
            return GenderHelper.translateEnumToString(getGender());
        }
        return null;
    }

    public List<String> getParentsStrings() {
        return Person.getPersonStringList(this.getParents());
    }

    public Set<Person> getParents() {
        return parents;
    }

    public void setParents(Set<Person> newParents) {
        for (Person parent : parents)
            parent.getChildren().remove(this);

        this.parents = new HashSet<>(newParents);

        for (Person parent : parents)
            parent.getChildren().add(this);
    }

    public void addParent(Person parent) {
        Set<Person> newParents = new HashSet<>(parents);
        newParents.add(parent);

        setParents(newParents);
    }

    public void removeParent(Person parent) {
        Set<Person> newParents = new HashSet<>(parents);
        newParents.remove(parent);

        setParents(newParents);
    }

    public void clearParents() {
        setParents(new HashSet<>());
    }

    public List<String> getGuardiansStrings() {
        return Person.getPersonStringList(this.getGuardians());
    }

    public Set<Person> getGuardians() {
        Set<Person> confirmedGuardians = new HashSet<>();
        for(Person guardian: guardians){
        	if(guardian.isActivated()){
               confirmedGuardians.add(guardian);
            }
        }
    	return confirmedGuardians;
    }

    public void setGuardians(Set<Person> newGuardians) {
        for (Person guardian : guardians)
            guardian.getCharges().remove(this);

        this.guardians = new HashSet<>(newGuardians);

        for (Person guardian : guardians)
            guardian.getCharges().add(this);
    }

    public void addGuardian(Person guardian) {
        Set<Person> newGuardians = new HashSet<>(guardians);
        newGuardians.add(guardian);

        setGuardians(newGuardians);
    }

    public void removeGuardian(Person guardian) {
        Set<Person> newGuardians = new HashSet<>(guardians);
        newGuardians.remove(guardian);

        setGuardians(newGuardians);
    }

    public void clearGuardians() {
        setGuardians(new HashSet<>());
    }

    public Set<Child> getSiblings() {
        return siblings;
    }

    public void setSiblings(Set<Child> newSiblings) {
        for (Child sibling : siblings)
            sibling.getSiblings().remove(this);

        this.siblings = new HashSet<>(newSiblings);

        for (Child sibling : siblings)
            sibling.getSiblings().add(this);
    }

    public void addSibling(Child sibling) {
        Set<Child> newSiblings = new HashSet<>(siblings);
        newSiblings.add(sibling);

        setSiblings(newSiblings);
    }

    public void removeSibling(Child sibling) {
        Set<Child> newSiblings = new HashSet<>(siblings);
        newSiblings.remove(sibling);

        setSiblings(newSiblings);
    }

    public void clearSiblings() {
        setSiblings(new HashSet<>());
    }

    public Date getBirthday() {
        return birthday;
    }

    @Deprecated
    public void setBirthday(Date birthday) {
        this.birthday = birthday;
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

    public Set<DayOfWeek> getDefaultPresences() {
        return defaultPresences;
    }

    public Set<String> getDefaultPresencesString() {
        Set<String> presencesString = new HashSet<>();
        for (DayOfWeek presence : getDefaultPresences()) {
            presencesString.add(DayOfWeekHelper.translateEnumToString(presence));
        }
        return presencesString;

    }

    public void setDefaultPresences(Set<DayOfWeek> defaultPresences) {
        this.defaultPresences = new HashSet<>(defaultPresences);
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

    public String getAllergies() {
        return allergies;
    }

    public void setAllergies(String allergies) {
        this.allergies = allergies;
    }

    public Person getEmergencyContact() {
        return emergencyContact;
    }

    public void setEmergencyContact(Person emergencyContact) {
        this.emergencyContact = emergencyContact;
    }

    public boolean isActive() {
        Calendar currentCalendar = GregorianCalendar.getInstance();

        Date currentDate = new Date(currentCalendar.get(Calendar.YEAR) - 1900,
                currentCalendar.get(Calendar.MONTH),
                currentCalendar.get(Calendar.DAY_OF_MONTH));

        return ((currentDate.after(registrationDate) || currentDate.equals(registrationDate)) &&
                (currentDate.before(deRegistrationDate)) || currentDate.equals(deRegistrationDate));
    }

}
