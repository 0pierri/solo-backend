package com.fdmgroup.backend.controller;

import com.fdmgroup.backend.exception.InvalidRequestException;
import com.fdmgroup.backend.exception.NotFoundException;
import com.fdmgroup.backend.exception.ServerException;
import com.fdmgroup.backend.model.TaskDTO;
import com.fdmgroup.backend.security.UserDetailsImpl;
import com.fdmgroup.backend.service.TaskService;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Objects;

/**
 * For task creation, see {@link ListController#createTask(long)}
 */
@AllArgsConstructor
@RestController
@RequestMapping("/tasks")
public class TaskController {

    private final TaskService service;

    @GetMapping()
    public Iterable<TaskDTO> getTasks() {
        Long userId = UserDetailsImpl
                .getCurrentUser()
                .map(UserDetailsImpl::getId)
                .orElseThrow(() -> new ServerException("No user found for current session"));
        return service.findAllByOwnerId(userId);
    }

    @SuppressWarnings("SpringElInspection")
    @PatchMapping("/{id}")
    @PreAuthorize("hasRole(T(UserRole).ADMIN) or hasPermission(#id, 'Task', T(Permission).WRITE)")
    public TaskDTO updateTask(@PathVariable long id, @Valid @RequestBody TaskDTO taskDTO, BindingResult result) {
        if (result.hasErrors() && result.hasFieldErrors()) {
            throw new InvalidRequestException(
                    Objects.requireNonNull(result.getFieldError()).getDefaultMessage());
        }
        taskDTO.setId(id);
        return service
                .update(taskDTO)
                .orElseThrow(() -> new InvalidRequestException("Invalid parameters"));
    }

    @SuppressWarnings("SpringElInspection")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole(T(UserRole).ADMIN) or hasPermission(#id, 'Task', T(Permission).WRITE)")
    public void deleteTask(@PathVariable long id) {
        if (!service.deleteById(id)) {
            throw new NotFoundException("Task with ID '" + id + "' not found");
        }
    }
}
