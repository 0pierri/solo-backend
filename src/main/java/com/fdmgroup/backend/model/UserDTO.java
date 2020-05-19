package com.fdmgroup.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fdmgroup.backend.security.UserRole;
import lombok.*;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class UserDTO implements DTO<User, Long> {

    public static final int MAX_FIELD_LENGTH = 255;
    public static final int MIN_PASSWORD_LENGTH = 8;

    @JsonIgnore
    private Long id;

    @Length(min = 1, max = MAX_FIELD_LENGTH, message = "Email must contain between 1 and " + MAX_FIELD_LENGTH + " characters")
    @NotBlank(message = "Email must not be blank")
    private String email;

    @Length(min = 1, max = MAX_FIELD_LENGTH, message = "Username must contain between 1 and " + MAX_FIELD_LENGTH + " characters")
    @NotBlank(message = "Username must not be blank")
    private String username;

    @Length(min = MIN_PASSWORD_LENGTH, message = "Password must contain at least " + MIN_PASSWORD_LENGTH + " characters")
    @Length(max = MAX_FIELD_LENGTH, message = "Password must contain between " + MIN_PASSWORD_LENGTH + " and " + MAX_FIELD_LENGTH + " characters")
    @NotBlank(message = "Password must not be blank")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    @JsonIgnore
    private UserRole.Role role;

    public UserDTO(String email, String username, String password) {
        this(email, username, password, UserRole.Role.ROLE_USER);
    }

    public UserDTO(String email, String username, String password, UserRole.Role role) {
        this.email = email;
        this.username = username;
        this.password = password;
        this.role = role;
    }

}
