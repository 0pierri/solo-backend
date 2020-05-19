package com.fdmgroup.backend.model;

import com.fdmgroup.backend.security.UserRole;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@SequenceGenerator(name="entity_gen", sequenceName = "USER_ID_SEQ")
public class User extends AbstractEntity<Long> {

    @NotNull
    private String email;

    @NotNull
    private String username;

    @NotNull
    private String password;

    @Enumerated(EnumType.STRING)
    @NotNull
    private UserRole.Role role;

    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TaskList> lists;

    @ManyToMany(mappedBy = "viewers")
    @OrderBy("id") // Keep order consistent when retrieved
    private Set<TaskList> sharedLists = new HashSet<>();


    public User(Long id) {
        setId(id);
    }

    public User(String email, String username, String password) {
        this(email, username, password, UserRole.Role.ROLE_USER, new ArrayList<>(), new HashSet<>());
    }

}
