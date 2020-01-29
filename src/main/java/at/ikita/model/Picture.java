package at.ikita.model;

import java.util.Objects;
import java.util.Set;

import javax.persistence.*;

import org.springframework.data.domain.Persistable;


/**
 * Entity represents a path for a Picture
 *
 * @author Lucas
 * @author Fabio.Valentini@student.uibk.ac.at
 */

@Entity
public class Picture implements Persistable<Integer> {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @Column(length = 511, unique = true, nullable = false)
    private String URI;

    @OneToMany(targetEntity = Person.class, mappedBy = "picture",
            cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.REMOVE})
    private Set<Person> persons;

    @OneToMany(targetEntity = Child.class, mappedBy = "picture",
            cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.REMOVE})
    private Set<Child> children;

    // Make sure to set newness to true in all constructors.
    @Transient
    private boolean newness = false;


    public static Picture create(String URI) {
        Picture picture = new Picture();
        picture.URI = URI;
        picture.newness = true;
        return picture;
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
        return String.format("[Picture]: %s", URI);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null)
            return false;

        if (!(o instanceof Picture))
            return false;

        final Picture entry = (Picture) o;

        // check if IDs are the same
        if ((id != 0) && (entry.getId() != 0)) {
            return (id == entry.getId());
        }

        // otherwise, identifying attributes must be identical
        return this.URI.equals(entry.URI);
    }

    @Override
    public int hashCode() {
        return Objects.hash(URI);
    }

    public String getURI() {
        return URI;
    }


	public void setURI(String uRI) {
		URI = uRI;
	}

}
