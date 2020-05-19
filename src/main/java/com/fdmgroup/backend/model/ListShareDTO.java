package com.fdmgroup.backend.model;

import lombok.*;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

import static com.fdmgroup.backend.model.UserDTO.MAX_FIELD_LENGTH;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ListShareDTO {

    @Length(min=1, max=255, message="Username must contain between 1 and " + MAX_FIELD_LENGTH + " characters")
    @NotBlank(message = "Username must not be blank")
    private String username;

}
