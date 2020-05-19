package com.fdmgroup.backend.service;

import com.fdmgroup.backend.exception.NotFoundException;
import com.fdmgroup.backend.model.AbstractEntity;
import com.fdmgroup.backend.model.DTO;
import com.fdmgroup.backend.model.User;
import com.fdmgroup.backend.model.ViewableEntity;
import com.fdmgroup.backend.repository.UserRepository;
import com.fdmgroup.backend.repository.ViewableEntityRepository;
import org.modelmapper.TypeMap;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Represents a {@code @Service} that provides {@code addViewer} and {@code removeViewer}
 * methods for its entities. Services that implement this should also extend {@link AbstractService}.
 * @param <E> the {@code @Entity} type
 * @param <I> the {@code @Id} type for the {@code @Entity}
 */
public interface ViewableEntityService<
        E extends AbstractEntity<I> & ViewableEntity,
        D extends DTO<E, I>,
        I extends Serializable> {

    /**
     * @return the UserRepository for this application
     */
    UserRepository getUserRepository();

    /**
     * @return the repository for this service's entities
     */
    ViewableEntityRepository<E, I> getRepository();

    /**
     * @return a {@link TypeMap} for mapping an {@code E} to a {@code D}
     */
    TypeMap<E, D> getDTOMapper();

    /**
     * @param viewerId the ID of the viewer
     * @return a collection of {@code DTO}s representing all entities viewable by {@code viewer}
     */
    default Iterable<D> findAllByViewerId(Long viewerId) {
        ArrayList<D> ds = new ArrayList<>();
        getRepository().findAllByViewers_Id(viewerId).forEach(e -> ds.add(getDTOMapper().map(e)));
        return ds;
    }

    /**
     * @param entityId the ID of the entity
     * @param viewerId the ID of the viewer
     * @return true if an entity exists with the specified viewer
     */
    default boolean existsByIdAndViewerId(I entityId, Long viewerId) {
        return getRepository().existsByIdAndViewers_Id(entityId, viewerId);
    }

    /**
     * @param entityId the ID of the entity
     * @param username the username of the viewer
     * @return true if an entity exists with the specified viewer
     */
    default boolean existsByIdAndViewerUsername(I entityId, String username) {
        return getRepository().existsByIdAndViewers_Username(entityId, username);
    }

    /**
     * Adds a viewer to the specified entity. Must be called from a transactional method.
     * @param entityId the ID of the entity
     * @param username the username of the user to add as a viewer
     * @return true if the entity existed, false otherwise.
     * Throws {@link NotFoundException} if the user cannot be found.
     */
    default boolean addViewer(I entityId, String username) {
        User user = getUserRepository()
                .findByUsername(username)
                .orElseThrow(() -> new NotFoundException("User '" + username + "' not found"));
        return getRepository()
                .findById(entityId)
                .map(e -> {
                    e.addViewer(user);
                    return true;
                })
                .orElse(false);
    }

    /**
     * Removes a viewer from the specified entity. Must be called from a transactional method.
     * @param entityId the ID of the entity
     * @param viewerId the ID of the user to remove as a viewer
     * @return true if the entity existed, false otherwise.
     * Throws {@link NotFoundException} if the user cannot be found.
     */
    default boolean removeViewer(I entityId, Long viewerId) {
        User viewer = getUserRepository()
                .findById(viewerId)
                .orElseThrow(() -> new NotFoundException("User with ID '" + viewerId + "' not found"));
        return getRepository()
                .findById(entityId)
                .map(e -> {
                    e.removeViewer(viewer);
                    return true;
                })
                .orElse(false);
    }

    /**
     * Removes all viewers from the specified entity. Must be called from a transactional method.
     * @param entityId the ID of the entity
     * @return true if the entity existed, false otherwise
     */
    default boolean removeAllViewers(I entityId) {
        return getRepository()
                .findById(entityId)
                .map(e -> {
                    e.getViewers().clear();
                    return true;
                })
                .orElse(false);
    }

}
