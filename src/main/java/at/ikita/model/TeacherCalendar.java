package at.ikita.model;

import java.util.Date;
import java.util.Objects;

import javax.persistence.*;

import org.springframework.data.domain.Persistable;
import org.springframework.util.Assert;


/**
 * Entity representing a day in the life of a Teacher
 *
 * @author Fabio.Valentini@student.uibk.ac.at
 */

@Entity
public class TeacherCalendar implements Persistable<Integer> {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @Column(nullable = false)
    @Temporal(TemporalType.DATE)
    private Date date;

    @ManyToOne(targetEntity = Person.class, optional = false,
            cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.REFRESH})
    private Person teacher;

    private boolean lunch;

    // Make sure to set newness to true in all constructors.
    @Transient
    private boolean newness = false;


    public static TeacherCalendar create(Date date, Person teacher) {
        TeacherCalendar entry = new TeacherCalendar();
        entry.date = date;
        entry.teacher = teacher;
        entry.newness = true;
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
        return String.format("[TeacherCalendar]: %s %s (%s)",
                teacher.getLastName(), teacher.getFirstName(), date.toString());
    }

    @Override
    public boolean equals(Object o) {
        if (o == null)
            return false;

        if (!(o instanceof TeacherCalendar))
            return false;

        final TeacherCalendar entry = (TeacherCalendar) o;

        // check if IDs are the same
        if ((id != 0) && (entry.getId() != 0)) {
            return (id == entry.getId());
        }

        // otherwise, identifying attributes must be identical
        return (this.teacher.equals(entry.teacher) &&
                this.date.equals(entry.date));
    }

    @Override
    public int hashCode() {
        return Objects.hash(teacher, date);
    }

    public Date getDate() {
        return date;
    }
    
    public Person getTeacher() {
        return teacher;
    }

    public boolean hasLunch() {
        return lunch;
    }

    public void setLunch(boolean lunch) {
        this.lunch = lunch;
    }
}
