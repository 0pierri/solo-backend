package com.fdmgroup.backend.repository;

import com.fdmgroup.backend.model.ViewableEntity;
import org.springframework.data.repository.CrudRepository;

import java.io.Serializable;

/**
 * Represents a {@code CrudRepository} for entities that implement {@link ViewableEntity}.
 * @param <E> the {@code @Entity} type
 * @param <I> the {@code @Id} type for the {@code @Entity}
 */
public interface ViewableEntityRepository<
        E extends ViewableEntity,
        I extends Serializable> extends CrudRepository<E, I> {

    /**
     * Returns all entities that can be viewed by the specified viewer.
     * @param viewerId the ID of the viewer
     * @return all entities that can be viewed by {@code viewer}
     */
    Iterable<E> findAllByViewers_Id(Long viewerId);

    /**
     * Returns whether or not an entity exists with the specified ID
     * that can be viewed by the specified user.
     * @param id the ID of the entity
     * @param viewerId the ID of the user
     * @return true if such an entity exists
     */
    boolean existsByIdAndViewers_Id(I id, Long viewerId);

    /**
     * Returns whether or not an entity exists with the specified ID
     * that can be viewed by the specified user.
     * @param id the ID of the entity
     * @param username the username of the user
     * @return true if such an entity exists
     */
    boolean existsByIdAndViewers_Username(I id, String username);

}
