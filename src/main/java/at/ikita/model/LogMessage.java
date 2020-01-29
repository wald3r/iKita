package at.ikita.model;

import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;

import javax.persistence.*;

import at.ikita.helper.LogMessageHelper;
import org.hibernate.annotations.GenericGenerator;

import org.springframework.data.domain.Persistable;
import org.springframework.format.datetime.DateFormatter;


/**
 * Entity represents a path for a Picture
 *
 * @author Lucas
 * @author Fabio.Valentini@student.uibk.ac.at
 */

@Entity
public class LogMessage implements Persistable<UUID> {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    @Column(updatable = false, nullable = false)
    private UUID id;

    @Temporal(TemporalType.TIMESTAMP)
    private Date time;

    @Enumerated(EnumType.STRING)
    private LogMessageType type;

    @Column(nullable = false)
    private String origin;

    @Column(nullable = false)
    private String message;

    @Transient
    private boolean newness = false;


    public static LogMessage create(LogMessageType type, Class origin, String message) {
        LogMessage entry = new LogMessage();

        entry.time = new Date();
        entry.type = type;
        entry.origin = origin.getTypeName();
        entry.message = message;

        entry.newness = true;
        return entry;
    }


    public UUID getId() {
        return id;
    }

    public boolean isNew() {
        return newness;
    }


    @Override
    public String toString() {
        return String.format("[%s] [ikita:%s] [%s]: %s",
                time.toString(),
                origin,
                LogMessageHelper.translateEnumToString(type),
                message);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null)
            return false;

        if (!(o instanceof LogMessage))
            return false;

        final LogMessage entry = (LogMessage) o;

        // check if IDs are the same
        if ((id != null) && (entry.getId() != null)) {
            return (id.equals(entry.getId()));
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, time, type, origin, message);
    }

    public Date getTime() {
        return time;
    }

    public LogMessageType getType() {
        return type;
    }

    public String getOrigin() {
        return origin;
    }

    public String getMessage() {
        return message;
    }

}
