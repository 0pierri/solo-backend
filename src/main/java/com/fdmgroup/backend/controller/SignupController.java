package com.fdmgroup.backend.controller;

import com.fdmgroup.backend.exception.InvalidRequestException;
import com.fdmgroup.backend.model.UserDTO;
import com.fdmgroup.backend.security.UserRole;
import com.fdmgroup.backend.service.UserService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Objects;

@Log4j2
@RestController
@DependsOn({"passwordEncoder"})
public class SignupController {

    private final UserService service;
    private final BCryptPasswordEncoder encoder;

    @Autowired
    public SignupController(UserService service, BCryptPasswordEncoder encoder) {
        this.service = service;
        this.encoder = encoder;
    }

    @PostMapping(value = "/signup")
    @ResponseStatus(HttpStatus.CREATED)
    public UserDTO handleSignup(@Valid @RequestBody UserDTO userDTO, BindingResult result) {
        if (result.hasErrors() && result.hasFieldErrors()) {
            throw new InvalidRequestException(
                    Objects.requireNonNull(result.getFieldError()).getDefaultMessage());
        }
        userDTO.setRole(UserRole.Role.ROLE_USER);
        userDTO.setPassword(encoder.encode(userDTO.getPassword()));
        log.debug("About to register new user '" + userDTO.getUsername() + "' [" + userDTO.getEmail() + "]");
        return service.create(userDTO);
    }

}
