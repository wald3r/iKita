package at.ikita.model;

import java.util.Date;
import java.util.Objects;

import javax.persistence.*;

import org.springframework.data.domain.Persistable;


/**
 * Entity representing one day in the Daycare Center
 *
 * @author Fabio.Valentini@student.uibk.ac.at
 */

@Entity
public class DayCareCalendar implements Persistable<Integer> {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @Column(nullable = false, unique = true)
    @Temporal(TemporalType.DATE)
    private Date date;

    @Column(nullable = false)
    @Temporal(TemporalType.TIME)
    private Date openingTime;

    @Column(nullable = false)
    @Temporal(TemporalType.TIME)
    private Date closingTime;

    @Column(nullable = true)
    private Integer maxAttendance;

    private String meal1Description;
    private String meal2Description;

    @Column(nullable = false)
    private Double mealPrice;

    // Make sure to set newness to true in all constructors.
    @Transient
    private boolean newness;


    public static DayCareCalendar create(Date date) {
        DayCareCalendar entry = new DayCareCalendar();
        entry.date = date;
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
        return String.format("[DayCareCalendar]: %s", date.toString());
    }

    @Override
    public boolean equals(Object o) {
        if (o == null)
            return false;

        if (!(o instanceof DayCareCalendar))
            return false;

        final DayCareCalendar entry = (DayCareCalendar) o;

        // check if IDs are the same
        if ((id != 0) && (entry.getId() != 0)) {
            return (id == entry.getId());
        }

        // otherwise, identifying attributes must be identical
        return this.date.equals(entry.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(date, openingTime, closingTime);
    }

    public Date getDate() {
        return date;
    }

    public Date getOpeningTime() {
        return openingTime;
    }

    public void setOpeningTime(Date openingTime) {
        this.openingTime = openingTime;
    }

    public Date getClosingTime() {
        return closingTime;
    }

    public void setClosingTime(Date closingTime) {
        this.closingTime = closingTime;
    }

    public int getMaxAttendance() {
        return maxAttendance;
    }

    public void setMaxAttendance(int maxAttendance) {
        this.maxAttendance = maxAttendance;
    }

    public String getMeal1Description() {
        return meal1Description;
    }

    public void setMeal1Description(String meal1Description) {
        this.meal1Description = meal1Description;
    }

    public String getMeal2Description() {
        return meal2Description;
    }

    public void setMeal2Description(String meal2Description) {
        this.meal2Description = meal2Description;
    }

    public Double getMealPrice() {
        return mealPrice;
    }

    public void setMealPrice(Double mealPrice) {
        this.mealPrice = mealPrice;
    }

    public boolean isClosed() {
        return (openingTime.getHours() == 0 &&
                openingTime.getMinutes() == 0 &&
                openingTime.getSeconds() == 0 &&
                closingTime.getHours() == 0 &&
                closingTime.getMinutes() == 0 &&
                closingTime.getSeconds() == 0);
    }

    public void setClosed() {
        Date zero = (Date) date.clone();

        zero.setHours(0);
        zero.setMinutes(0);
        zero.setSeconds(0);

        openingTime = zero;
        closingTime = zero;
    }
}
