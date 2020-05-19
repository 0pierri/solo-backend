package com.fdmgroup.backend.controller;

import com.fdmgroup.backend.exception.NotFoundException;
import com.fdmgroup.backend.exception.ServerException;
import com.fdmgroup.backend.model.UserDTO;
import com.fdmgroup.backend.security.UserDetailsImpl;
import com.fdmgroup.backend.service.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

@Log4j2
@AllArgsConstructor
@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    @GetMapping
    @Transactional
    public UserDTO getProfile() {
        return UserDetailsImpl.getCurrentUser()
                .map(u -> userService
                        .findById(u.getId())
                        .orElseThrow(() -> new NotFoundException("User with ID '" + u.getId() + "' not found")))
                .orElseThrow(() -> new ServerException("No user found for current session"));
    }

    @DeleteMapping
    @Transactional
    public void deleteAccount(HttpServletRequest request) {
        Long userId = UserDetailsImpl.getCurrentUser()
                .map(UserDetailsImpl::getId)
                .orElseThrow(() -> new ServerException("No user found for current session"));
        if (!userService.deleteById(userId)) {
            throw new NotFoundException("User with ID '" + userId + "' not found");
        }
        log.debug("Deleted account ID '" + userId + "'");
        try {
            request.logout();
        } catch (ServletException e) {
            throw new ServerException("Logout of user '" + userId + "' failed");
        }
    }

}
