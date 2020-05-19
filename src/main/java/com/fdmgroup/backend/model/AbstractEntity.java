package com.fdmgroup.backend.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import java.io.Serializable;

/**
 * Represents an {@code @Entity}.
 * <br>
 * Subclasses should include a {@code @SequenceGenerator(name="entity_gen")} annotation.
 * <br>
 * For example: {@code @SequenceGenerator(name="entity_gen", sequenceName = "MY_ENTITY_ID_SEQ")}
 * @param <I> the {@code @Id} type for the {@code @Entity}
 */
@MappedSuperclass
@Getter
@Setter
@NoArgsConstructor
public abstract class AbstractEntity<I extends Serializable> {

    @Id
    @GeneratedValue(generator = "entity_gen")
    private I id;

}
