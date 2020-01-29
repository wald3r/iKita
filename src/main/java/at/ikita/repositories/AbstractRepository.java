package at.ikita.repositories;

import java.io.Serializable;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.Repository;


/**
 * Abstract Repository definition.
 *
 * @author Fabio.Valentini@student.uibk.ac.at
 */

@NoRepositoryBean
public interface AbstractRepository<T, ID extends Serializable> extends Repository<T, ID> {

    @Transactional
    void delete(T entity);

    List<T> findAll();

    T findOne(ID id);

    <S extends T> S save(S entity);

}
