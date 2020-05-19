package com.fdmgroup.backend.repository;

import com.fdmgroup.backend.model.OwnedEntity;
import org.springframework.data.repository.CrudRepository;

import java.io.Serializable;

/**
 * Represents a {@code CrudRepository} for entities that implement {@link OwnedEntity}.
 * @param <E> the {@code @Entity} type
 * @param <I> the {@code @Id} type for the {@code @Entity}
 */
public interface OwnedEntityRepository<
        E extends OwnedEntity,
        I extends Serializable> extends CrudRepository<E, I> {

    /**
     * Returns all entities that have the specified owner.
     * Sub-interfaces may need to override this with a method that performs any necessary joins.
     * @param ownerId the ID of the owner
     * @return all entities owned by {@code owner}
     */
    Iterable<E> findAllByOwner_Id(Long ownerId);

    /**
     * Returns whether or not an entity exists with the specified ID and owner.
     * Sub-interfaces may need to override this with a method that performs any necessary joins.
     * @param id the ID of the entity
     * @param ownerId the ID of the owner
     * @return true if an entity exists with the specified ID and owner
     */
    boolean existsByIdAndOwner_Id(I id, Long ownerId);

    /**
     * Deletes all entities with the specified owner
     * @param ownerId the ID of the owner
     */
    void deleteAllByOwner_Id(Long ownerId);

}
