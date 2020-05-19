package com.fdmgroup.backend.service;

import com.fdmgroup.backend.model.AbstractEntity;
import com.fdmgroup.backend.model.DTO;
import com.fdmgroup.backend.model.OwnedEntity;
import com.fdmgroup.backend.repository.OwnedEntityRepository;
import org.modelmapper.TypeMap;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Represents a {@code @Service} that provides a {@code findAllByOwner} method for its entities.
 * Services that implement this should also extend {@link AbstractService}.
 * @param <E> the {@code @Entity} type
 * @param <D> the {@code DTO} type for the {@code @Entity}
 * @param <I> the {@code @Id} type for the {@code @Entity}
 */
interface OwnedEntityService<
        E extends AbstractEntity<I> & OwnedEntity,
        D extends DTO<E, I>,
        I extends Serializable> {

    /**
     * @return the repository for this service's entities
     */
    OwnedEntityRepository<E, I> getRepository();

    /**
     * @return a {@link TypeMap} for mapping an {@code E} to a {@code D}
     */
    TypeMap<E, D> getDTOMapper();

    /**
     * @param ownerId the ID of the owner
     * @return a collection of {@code DTO}s representing all entities owned by {@code owner}
     */
    @Transactional(readOnly = true)
    default Iterable<D> findAllByOwnerId(Long ownerId) {
        ArrayList<D> ds = new ArrayList<>();
        getRepository().findAllByOwner_Id(ownerId).forEach(e -> ds.add(getDTOMapper().map(e)));
        return ds;
    }

    /**
     * Deletes all entities with the specified owner.
     * @param ownerId the ID of the owner
     */
    default void deleteAllByOwnerId(Long ownerId) {
        getRepository().deleteAllByOwner_Id(ownerId);
    }

}
