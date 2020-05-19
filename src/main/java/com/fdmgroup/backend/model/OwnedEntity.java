package com.fdmgroup.backend.model;

/**
 * Represents an entity that has an owner, and provides
 * a {@code getOwner()} method to retrieve it.
 */
public interface OwnedEntity {

    /**
     * @return the owner of this entity
     */
    User getOwner();

}
