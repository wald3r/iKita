package at.ikita.model;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

import javax.persistence.*;

import org.springframework.data.domain.Persistable;


/**
 * Entity representing one day in the life of a Child
 *
 * @author Fabio.Valentini@student.uibk.ac.at
 */

@Entity
public class ChildCalendar implements Persistable<Integer> {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @Column(nullable = false)
    @Temporal(TemporalType.DATE)
    private Date date;

    @ManyToOne(targetEntity = Child.class, fetch = FetchType.EAGER, optional = false,
            cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.REFRESH})
    private Child child;

    private boolean lunch;

    @Temporal(TemporalType.TIME)
    private Date bringTime;

    @Temporal(TemporalType.TIME)
    private Date pickupTime;

    @ManyToOne(targetEntity = Person.class, fetch = FetchType.EAGER,
            cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.REFRESH})
    private Person pickupPerson;

    // Make sure to set newness to true in all constructors.
    @Transient
    private boolean newness = false;


    public static ChildCalendar create(Date date, Child Child) {
        ChildCalendar entry = new ChildCalendar();

        entry.date = date;
        entry.child = Child;
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
        return String.format("[ChildCalendar]: %s on %s", child.toString(), date.toString());
    }

    @Override
    public boolean equals(Object o) {
        if (o == null)
            return false;

        if (!(o instanceof ChildCalendar))
            return false;

        final ChildCalendar entry = (ChildCalendar) o;

        // check if IDs are the same
        if ((id != 0) && (entry.getId() != 0)) {
            return (id == entry.getId());
        }

        // otherwise, identifying attributes must be identical
        return (this.child.equals(entry.child) &&
                this.date.equals(entry.date));
    }

    @Override
    public int hashCode() {
        return Objects.hash(child, date);
    }

    public Date getDate() {
        return date;
    }

    public Child getChild() {
        return child;
    }

    public boolean hasLunch() {
        return lunch;
    }

    public boolean isLunch() {
        return lunch;
    }

    public void setLunch(boolean lunch) {
        this.lunch = lunch;
    }

    public String getLunch() {
        if(lunch) return "x";
        else return "";
    }

    public void setLunch(String lunch) {
        this.lunch = lunch.length() > 0;
    }

    public Date getBringTime() {
        return bringTime;
    }

    public void setBringTime(Date bringTime) {
        this.bringTime = bringTime;
    }

    public Date getPickupTime() {
        return pickupTime;
    }

    public void setPickupTime(Date pickupTime) {
        this.pickupTime = pickupTime;
    }

    public Person getPickupPerson() {
        return pickupPerson;
    }

    public void setPickupPerson(Person pickupPerson) {
        this.pickupPerson = pickupPerson;
    }

    public String getDateString() {
        SimpleDateFormat df = new SimpleDateFormat("dd. MMMM YYYY");
        return df.format(date);
    }

    public String getBringTimeString() {
        return bringTime.getHours() + ":" + bringTime.getMinutes();
    }

    public String getPickupTimeString() {
        return pickupTime.getHours() + ":" + pickupTime.getMinutes();
    }
}
