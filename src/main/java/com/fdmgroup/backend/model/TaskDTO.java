package com.fdmgroup.backend.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.hibernate.validator.constraints.Length;

import static com.fasterxml.jackson.annotation.JsonProperty.Access;

@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class TaskDTO implements DTO<Task, Long> {

    private static final int MAX_FIELD_LENGTH = 255;

    @JsonProperty(access = Access.READ_ONLY)
    @EqualsAndHashCode.Include
    private Long id;
    @JsonProperty(access = Access.READ_ONLY)
    @EqualsAndHashCode.Include
    private Long listId;
    @Length(max=MAX_FIELD_LENGTH, message = "Name can contain at most " + MAX_FIELD_LENGTH + " characters")
    private String name;
    private Task.State state;
    @JsonProperty(access = Access.READ_ONLY)
    private Long createdAt;
    @JsonProperty(access = Access.READ_ONLY)
    private final Long updatedAt = System.currentTimeMillis();

}
