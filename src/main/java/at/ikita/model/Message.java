package at.ikita.model;

import java.util.*;

import javax.persistence.*;

import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.domain.Persistable;


/**
 * Entity representing a Message (P2P or broadcast)
 *
 * @author Fabio.Valentini@student.uibk.ac.at
 */

@Entity
public class Message implements Persistable<UUID> {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    @Column(updatable = false, nullable = false)
    private UUID id;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private Date time;

    @Column(nullable = false)
    private String subject;

    @Column(nullable = false, length = 511)
    private String content;

    @ManyToOne(targetEntity = Message.class,
            cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.REFRESH})
    private Message responseTo;

    @OneToMany(targetEntity = Message.class,
            mappedBy = "responseTo",
            cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.REMOVE},
            orphanRemoval = true)
    private Set<Message> responses;

    @ManyToOne(targetEntity = Person.class,
            fetch = FetchType.EAGER,
            optional = false,
            cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.REFRESH})
    private Person sender;

    @ManyToOne(targetEntity = Person.class,
            fetch = FetchType.EAGER,
            optional = true,
            cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.REFRESH})
    private Person recipient;

    private boolean broadcast;
    private boolean read = false;

    @Transient
    private boolean newness = false;


    public static Message create(Person sender, Person recipient, String subject, String content) {
        Message message = new Message();

        message.sender = sender;
        message.recipient = recipient;
        message.subject = subject;
        message.content = content;
        message.broadcast = false;

        message.responses = new HashSet<>();

        message.newness = true;
        return message;
    }


    public static Message createResponse(Person sender, Message original, String content) {
        Message message = new Message();

        message.sender = sender;
        message.recipient = original.getSender();
        message.subject = original.getSubject();
        message.content = content;
        message.responseTo = original;

        message.responses = new HashSet<>();

        message.newness = true;
        return message;
    }


    public static Message createBroadcast(Person sender, String subject, String content) {
        Message message = new Message();

        message.sender = sender;
        message.recipient = null;
        message.subject = subject;
        message.content = content;
        message.broadcast = true;

        message.responses = new HashSet<>();

        message.newness = true;
        return message;
    }


    // Only Getters and Setters below this point, nothing to see here.
    @Override
    public UUID getId() {
        return id;
    }

    @Override
    public boolean isNew() {
        return newness;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null)
            return false;

        if (!(o instanceof Message))
            return false;

        final Message entry = (Message) o;

        // check if IDs are the same
        return (id != null) && (entry.getId() != null) && (id.equals(entry.getId()));

    }

    @Override
    public int hashCode() {
        return Objects.hash(id, subject, content, recipient, sender);
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public String getSubject() {
        return subject;
    }

    public String getContent() {
        return content;
    }

    public Message getResponseTo() {
        return responseTo;
    }

    public Set<Message> getResponses() {
        return responses;
    }

    public Person getSender() {
        return sender;
    }

    public Person getRecipient() {
        return recipient;
    }

    public boolean isBroadcast() {
        return broadcast;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

}
