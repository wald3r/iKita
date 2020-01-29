package at.ikita.repositories;

import at.ikita.model.Picture;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


/**
 * Repository for managing {@link Picture} entities.
 *
 * @author Fabio.Valentini@student.uibk.ac.at
 */

public interface PictureRepository extends AbstractRepository<Picture, Integer> {

    @Query("SELECT p FROM Picture p WHERE p.URI = :uri")
    Picture findByURI(@Param("uri") String uri);

}
