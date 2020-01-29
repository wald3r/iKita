package at.ikita.model;

import java.util.*;

import javax.persistence.*;

import at.ikita.helper.PersonRoleHelper;

import org.springframework.data.domain.Persistable;
import org.springframework.format.datetime.DateFormatter;
import org.springframework.util.Assert;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;


/**
 * Entity represents persons which have access to the system
 *
 * @author Andre.Potocnik@student.uibk.ac.at
 * @author Fabio.Valentini@student.uibk.ac.at
 * @author Kerstin.Klabischnig@student.uibk.ac.at
 */

@Entity
public class Person implements Persistable<Integer> {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @Column(length = 80, nullable = false, unique = true)
    private String email;

    @Column(length = 80, nullable = false)
    private String lastName;

    @Column(length = 80, nullable = false)
    private String firstName;

    @Column(length = 60, nullable = false)
    private String password;

    @Column(length = 24)
    private String privateTelephone;
    @Column(length = 24)
    private String workTelephone;

    @ManyToOne(targetEntity = Picture.class, fetch = FetchType.EAGER,
            cascade = {CascadeType.MERGE, CascadeType.REFRESH, CascadeType.REMOVE})
    private Picture picture;

    @Column(length = 140)
    private String comment;

    private boolean mailNotification;
    private boolean mailLunchReminder;

    @ManyToMany(fetch = FetchType.EAGER,
            cascade = {CascadeType.MERGE, CascadeType.REFRESH})
    @JoinTable(name = "PARENT_CHILDREN",
            joinColumns = @JoinColumn(name = "PARENT_ID", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "CHILD_ID", referencedColumnName = "id"))
    private Set<Child> children;

    @ManyToMany(mappedBy = "guardians", fetch = FetchType.EAGER,
            cascade = {CascadeType.MERGE, CascadeType.REFRESH})
    private Set<Child> charges;

    @OneToMany(targetEntity = ParentTask.class, mappedBy = "person", orphanRemoval = true,
            cascade = {CascadeType.MERGE, CascadeType.REFRESH, CascadeType.REMOVE})
    private Set<ParentTask> tasks;

    @OneToMany(targetEntity = TeacherCalendar.class, mappedBy = "teacher", orphanRemoval = true,
            cascade = {CascadeType.MERGE, CascadeType.REFRESH, CascadeType.REMOVE})
    private Set<TeacherCalendar> teacherCalendarEntries;

    @OneToMany(targetEntity = ChildCalendar.class, mappedBy = "pickupPerson", orphanRemoval = true,
            cascade = {CascadeType.MERGE, CascadeType.REFRESH, CascadeType.REMOVE})
    private Set<ChildCalendar> pickupAgreements;

    @OneToMany(targetEntity = Message.class, mappedBy = "sender", orphanRemoval = true,
            cascade = {CascadeType.MERGE, CascadeType.REFRESH, CascadeType.REMOVE})
    private Set<Message> sentMessages;

    @OneToMany(targetEntity = Message.class, mappedBy = "recipient", orphanRemoval = true,
            cascade = {CascadeType.MERGE, CascadeType.REFRESH, CascadeType.REMOVE})
    private Set<Message> receivedMessages;

    @ElementCollection(targetClass = PersonRole.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "Person_PersonRole")
    @Enumerated(EnumType.STRING)
    private Set<PersonRole> roles;

    /**
     * This attribute is true if a parent has active children in the Day-Care Center.
     */
    private boolean active;

    /**
     * This attribute is true if a guardian has been confirmed by a staff member.
     */
    private boolean activated;

    /**
     * This attribute is used to determine whether this Person can log in or not.
     * It is true for almost everybody, except for guardians who have no other roles than GUARDIAN.
     */
    private boolean enabled;

    // Make sure to set newness to true in all constructors.
    @Transient
    private boolean newness = false;


    public static Person create(String email) {
        Person person = new Person();

        person.email = email;

        person.children = new HashSet<>();
        person.charges = new HashSet<>();
        person.tasks = new HashSet<>();

        person.teacherCalendarEntries = new HashSet<>();
        person.pickupAgreements = new HashSet<>();
        person.sentMessages = new HashSet<>();
        person.receivedMessages = new HashSet<>();

        person.roles = new HashSet<>();

        person.newness = true;
        return person;
    }

    public static Person create(String email, String lastName, String firstName,
                                String password, Set<PersonRole> roles) {
        Person person = Person.create(email);

        person.lastName = lastName;
        person.firstName = firstName;
        person.setPassword(password);
        person.roles = roles;

        person.enabled = roles.contains(PersonRole.ADMIN) || roles.contains(PersonRole.PARENT) || roles.contains(PersonRole.TEACHER);

        return person;
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
        return String.format("%s %s <%s>", firstName, lastName, email);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null)
            return false;

        if (!(o instanceof Person))
            return false;

        final Person entry = (Person) o;

        // check if IDs are the same
        if ((id != 0) && (entry.getId() != 0)) {
            return (id == entry.getId());
        }

        // otherwise, identifying attributes must be identical
        return (this.lastName.equals(entry.lastName) &&
                this.firstName.equals(entry.firstName) &&
                this.email.equals(entry.email));
    }

    @Override
    public int hashCode() {
        return Objects.hash(email, lastName, firstName);
    }

    public static List<String> getPersonStringList(Collection<Person> persons) {
        List<String> stringList = new ArrayList<>(persons.size());

        for (Person person : persons) {
            String name = person.getFirstName();
            name += " ";
            name += person.getLastName();
            stringList.add(name);
        }

        return stringList;
    }

    public List<String> getChildrenStringList() {
        Collection<Child> children = this.getChildren();
        List<String> stringList = new ArrayList<>(children.size());

        for (Child child : children) {
            String data = String.format("%s %s (%s)", child.getLastName(), child.getFirstName(),
                    new DateFormatter().print(child.getBirthday(), Locale.GERMAN));
            stringList.add(data);
        }
        return stringList;
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
        Assert.notNull(lastName);
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        Assert.notNull(firstName);
        this.firstName = firstName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        Assert.notNull(password);
        this.password = new BCryptPasswordEncoder().encode(password);
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

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Picture getPicture() {
        return picture;
    }

    public void setPicture(Picture picture) {
        this.picture = picture;
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

    public Set<Child> getChildren() {
        return children;
    }

    public void setChildren(Set<Child> newChildren) {
        for (Child child : children)
            child.getParents().remove(this);

        this.children = new HashSet<>(newChildren);

        for (Child child : children)
            child.getParents().add(this);
    }

    public void addChild(Child child) {
        Set<Child> newChildren = new HashSet<>(children);
        newChildren.add(child);

        setChildren(newChildren);
    }

    public void removeChild(Child child) {
        Set<Child> newChildren = new HashSet<>(children);
        newChildren.remove(child);

        setChildren(newChildren);
    }

    public void clearChildren() {
        setChildren(new HashSet<>());
    }

    public List<String> getChargesStrings() {
        return Child.getChildStringList(getCharges());
    }

    public Set<Child> getCharges() {
        return charges;
    }

    public void setCharges(Set<Child> newCharges) {
        for (Child child : charges)
            child.getGuardians().remove(this);

        this.charges = new HashSet<>(newCharges);

        for (Child child : charges)
            child.getGuardians().add(this);
    }

    public void addCharge(Child child) {
        Set<Child> newCharges = new HashSet<>(charges);
        newCharges.add(child);

        setCharges(newCharges);
    }

    public void removeCharge(Child child) {
        Set<Child> newCharges = new HashSet<>(charges);
        newCharges.remove(child);

        setCharges(newCharges);
    }

    public void clearCharges() {
        setCharges(new HashSet<>());
    }

    public Set<PersonRole> getRoles() {
        return roles;
    }

    public void setRoles(Set<PersonRole> roles) {
        this.roles = new HashSet<>(roles);
    }

    public String getRolesString() {
        Set<PersonRole> roles = getRoles();

        if (roles.size() == 0)
            return "Keine";

        StringBuilder sb = new StringBuilder();

        Iterator<PersonRole> iterator = roles.iterator();

        while (iterator.hasNext()) {
            sb.append(PersonRoleHelper.translateEnumToString(iterator.next()));

            if (iterator.hasNext())
                sb.append(", ");
        }

        return sb.toString();
    }

    public void addRole(PersonRole role) {
        // if an additional role is being given to a GUARDIAN, set account to enabled
        if (this.roles.contains(PersonRole.GUARDIAN) && (role != PersonRole.GUARDIAN))
            this.enabled = true;

        this.roles.add(role);
    }

    public void removeRole(PersonRole role) {
        this.roles.remove(role);

        // if after removing a role only GUARDIAN is left, set account to inactive
        if (this.roles.size() == 1 && this.roles.contains(PersonRole.GUARDIAN))
            this.enabled = false;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isActivated() {
        return activated;
    }

    public void setActivated(boolean activated) {
        this.activated = activated;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public Set<Person> getAllAssociatedGuardians() {
        Set<Person> guardians = new HashSet<>();

        for (Child child : getChildren()) {
            guardians.addAll(child.getGuardians());
        }

        return guardians;
    }

    public Set<Person> getAllAssociatedParents() {
        Set<Person> parents = new HashSet<>();

        for (Child child : getChildren()) {
            parents.addAll(child.getParents());
        }

        return parents;
    }

    public Set<Person> getAllAssociatedEmergencyContacts() {
        Set<Person> emergencyContacts = new HashSet<>();

        for (Child child : getChildren()) {
            emergencyContacts.add(child.getEmergencyContact());
        }

        return emergencyContacts;
    }
}
