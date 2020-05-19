package com.fdmgroup.backend.model;

import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SequenceGenerator(name="entity_gen", sequenceName = "TASK_ID_SEQ")
public class Task extends AbstractEntity<Long> implements OwnedEntity {

    public enum State {
        TASK_INBOX,
        TASK_ARCHIVED,
        TASK_PINNED
    }

    private String name;
    @Enumerated(EnumType.STRING)
    @NotNull
    private State state;
    @NotNull
    private long createdAt;
    @NotNull
    private long updatedAt;

    @ManyToOne
    @JsonIdentityReference(alwaysAsId = true)
    @JsonProperty("listId")
    @NotNull
    private TaskList list;

    public Task(String name, TaskList list) {
        this.name = name;
        this.list = list;
        this.state = State.TASK_INBOX;
        createdAt = System.currentTimeMillis();
        updatedAt = System.currentTimeMillis();
    }

    @Override
    public User getOwner() {
        return list.getOwner();
    }
}
