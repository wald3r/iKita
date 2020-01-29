package at.ikita.model;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Objects;

import javax.persistence.*;

import org.springframework.data.domain.Persistable;


/**
 * Entity representing a Task assigned to a Person (with PARENT role)
 *
 * @author kerstin
 * @author Fabio.Valentini@student.uibk.ac.at
 */

@Entity
public class ParentTask implements Persistable<Integer> {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @ManyToOne(targetEntity = Person.class, fetch = FetchType.EAGER, optional = false,
            cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.REFRESH})
    private Person person;

    @Temporal(TemporalType.DATE)
    @Column(nullable = false)
    private Date startDate;

    @Temporal(TemporalType.DATE)
    @Column(nullable = false)
    private Date endDate;

    @Column(length = 140, nullable = false)
    private String title;

    @Column(length = 320)
    private String description;

    private boolean urgent;
    private boolean done;

    // Make sure to set newness to true in all constructors.
    @Transient
    private boolean newness = false;


    public static ParentTask create() {
        ParentTask entry = new ParentTask();
        entry.newness = true;
        return entry;
    }

    public static ParentTask create(String title, Date startDate, Date endDate) {
        ParentTask entry = ParentTask.create();

        entry.title = title;
        entry.startDate = startDate;
        entry.endDate = endDate;

        return entry;
    }

    public static ParentTask create(Person parent, String title, Date startDate, Date endDate) {
        ParentTask entry = ParentTask.create(title, startDate, endDate);

        entry.person = parent;

        return entry;
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
        return String.format("[ParentTask]: %s (%s: %s-%s)",
                title, person.toString(), startDate.toString(), endDate.toString());
    }

    @Override
    public boolean equals(Object o) {
        if (o == null)
            return false;

        if (!(o instanceof ParentTask))
            return false;

        final ParentTask entry = (ParentTask) o;

        // check if IDs are the same
        if ((id != 0) && (entry.getId() != 0)) {
            return (id == entry.getId());
        }

        // otherwise, identifying attributes must be identical
        return (this.person.equals(entry.person) &&
                this.title.equals(entry.title) &&
                this.startDate.equals(entry.startDate) &&
                this.endDate.equals(entry.endDate));
    }

    @Override
    public int hashCode() {
        return Objects.hash(person, title, startDate, endDate);
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
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

    public boolean isUrgent() {
        return urgent;
    }

    public void setUrgent(boolean urgent) {
        this.urgent = urgent;
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isActive() {
        Calendar currentCalendar = GregorianCalendar.getInstance();

        Date currentDate = new Date(currentCalendar.get(Calendar.YEAR) - 1900,
                currentCalendar.get(Calendar.MONTH),
                currentCalendar.get(Calendar.DAY_OF_MONTH));

        return ((currentDate.after(startDate) || currentDate.equals(startDate)) &&
                (currentDate.before(endDate)) || currentDate.equals(endDate));
    }

}
