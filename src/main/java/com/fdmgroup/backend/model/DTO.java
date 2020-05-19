package com.fdmgroup.backend.model;

import java.io.Serializable;

/**
 * Represents a wrapper class for the transfer of data for an {@code @Entity}.
 * @param <E> the type of the {@code @Entity}
 * @param <I> the {@code @Id} type for the {@code @Entity}
 */
public interface DTO<E extends AbstractEntity<I>, I extends Serializable> {

    /**
     * @return the entity's ID
     */
    I getId();

    /**
     * Sets this {@code DTO}'s {@code id} field
     * @param id the ID to set
     */
    void setId(I id);

}
