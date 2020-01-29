package at.ikita.repositories;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import at.ikita.model.LogMessage;
import at.ikita.model.LogMessageType;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


/**
 * Repository for managing {@link LogMessage} entities.
 *
 * @author Fabio.Valentini@student.uibk.ac.at
 */

public interface LogMessageRepository extends AbstractRepository<LogMessage, UUID> {

    @Query("SELECT l FROM LogMessage l WHERE l.time > :after AND l.time < :before ORDER BY l.time DESC")
    List<LogMessage> findByTime(@Param("after") Date after, @Param("before") Date before);

    @Query("SELECT l FROM LogMessage l WHERE l.type = :type ORDER BY l.time DESC")
    List<LogMessage> findByType(@Param("type") LogMessageType type);

    @Query("SELECT l FROM LogMessage l WHERE l.time > :after AND l.time < :before AND l.type = :type ORDER BY l.time DESC")
    List<LogMessage> findByTimeAndType(@Param("after") Date after, @Param("before") Date before,
                                       @Param("type") LogMessageType type);
}
