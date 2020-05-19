package com.fdmgroup.backend.model;

import java.util.Set;

/**
 * Represents an entity that can be shared to allow other users to view it,
 * and provides {@code addViewer()} and {@code removeViewer()} methods to control this.
 */
public interface ViewableEntity {

    /**
     * @return this entity's viewers
     */
    Set<User> getViewers();

    /**
     * @param viewer the user that this entity will be shared with
     */
    void addViewer(User viewer);

    /**
     * @param viewer the user that this entity will no longer be shared with
     */
    void removeViewer(User viewer);

}
