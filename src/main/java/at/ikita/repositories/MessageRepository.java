package at.ikita.repositories;

import java.util.List;
import java.util.UUID;

import at.ikita.model.Message;

import at.ikita.model.Person;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


/**
 * Repository for managing {@link Message} entities.
 *
 * @author Fabio.Valentini@student.uibk.ac.at
 */

public interface MessageRepository extends AbstractRepository<Message, UUID> {

    @Query("SELECT m FROM Message m WHERE m.recipient = :recipient ORDER BY m.time DESC")
    List<Message> findByRecipient(@Param("recipient") Person recipient);

    @Query("SELECT m FROM Message m WHERE m.sender = :sender ORDER BY m.time DESC")
    List<Message> findBySender(@Param("sender") Person sender);

    @Query("SELECT m FROM Message m WHERE m.broadcast = true AND m.read = :read ORDER BY m.time DESC")
    List<Message> findBroadcasts(@Param("read") boolean read);

    @Query("SELECT m FROM Message m WHERE m.broadcast = true AND m.sender = :sender ORDER BY m.time DESC")
    List<Message> findMyBroadcasts(@Param("sender") Person sender);

    @Query("SELECT m FROM Message m WHERE m.subject = :subject ORDER BY m.time DESC")
    List<Message> findBySubject(@Param("subject") String subject);

    @Query("SELECT m FROM Message m WHERE m.recipient = :recipient AND m.read = :read ORDER BY m.time DESC")
    List<Message> findByRecipientAndRead(@Param("recipient") Person recipient,
                                         @Param("read") boolean read);

}
