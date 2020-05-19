package com.fdmgroup.backend.model;

import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SequenceGenerator(name="entity_gen", sequenceName = "LIST_ID_SEQ")
public class TaskList extends AbstractEntity<Long> implements OwnedEntity, ViewableEntity {

    private String name;

    @OneToMany(mappedBy = "list", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Task> tasks = new ArrayList<>();

    @ManyToOne
    @JsonIdentityReference(alwaysAsId = true)
    @JsonProperty("ownerId")
    @NotNull
    private User owner;

    // Intellij for some reason can't find the Task_List.ID column and thinks it's an error
    @SuppressWarnings("JpaDataSourceORMInspection")
    @ManyToMany(cascade = {
            CascadeType.PERSIST,
            CascadeType.MERGE
    })
    @JoinTable(name = "LIST_VIEWERS",
            joinColumns = @JoinColumn(name="LIST_ID", referencedColumnName = "ID"),
            inverseJoinColumns = @JoinColumn(name="VIEWER_ID", referencedColumnName = "ID"))
    private Set<User> viewers = new HashSet<>();

    public TaskList(String name, User owner) {
        this.name = name;
        this.owner = owner;
    }

    @Override
    public void addViewer(User viewer) {
        viewers.add(viewer);
        viewer.getSharedLists().remove(this);
    }

    @Override
    public void removeViewer(User viewer) {
        viewers.remove(viewer);
        viewer.getSharedLists().remove(this);
    }
}
