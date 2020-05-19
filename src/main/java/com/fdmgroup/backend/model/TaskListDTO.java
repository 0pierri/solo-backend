package com.fdmgroup.backend.model;

import com.fasterxml.jackson.annotation.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.validator.constraints.Length;

import java.util.List;
import java.util.Set;

import static com.fasterxml.jackson.annotation.JsonProperty.Access;

@Getter
@Setter
@ToString
@EqualsAndHashCode
public class TaskListDTO implements DTO<TaskList, Long> {

    private static final int MAX_FIELD_LENGTH = 255;

    @JsonProperty(access= Access.READ_ONLY)
    private Long id;

    @Length(max=MAX_FIELD_LENGTH, message = "Name can contain at most " + MAX_FIELD_LENGTH + " characters")
    private String name;

    @JsonProperty(access = Access.READ_ONLY)
    private List<TaskDTO> tasks;

    @JsonProperty(access = Access.READ_ONLY)
    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "username")
    @JsonIdentityReference(alwaysAsId = true)
    private Set<UserDTO> viewers;

    @JsonIgnore
    private Long ownerId;

}
