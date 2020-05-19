package com.fdmgroup.backend.controller;

import com.fdmgroup.backend.exception.InvalidRequestException;
import com.fdmgroup.backend.exception.NotFoundException;
import com.fdmgroup.backend.exception.ServerException;
import com.fdmgroup.backend.model.*;
import com.fdmgroup.backend.security.UserDetailsImpl;
import com.fdmgroup.backend.service.ListService;
import com.fdmgroup.backend.service.TaskService;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Log4j2
@AllArgsConstructor
@RestController
@RequestMapping(value = "/lists")
public class ListController {

    private final ListService listService;
    private final TaskService taskService;

    @GetMapping
    public Iterable<TaskListDTO> getLists() {
        Long userId = UserDetailsImpl
                .getCurrentUser()
                .map(UserDetailsImpl::getId)
                .orElseThrow(() -> new ServerException("No user found for current session"));
        return listService.findAllByOwnerId(userId);
    }

    @GetMapping("/shared")
    @Transactional
    public Iterable<TaskListDTO> getSharedLists() {
        Long userId = UserDetailsImpl
                .getCurrentUser()
                .map(UserDetailsImpl::getId)
                .orElseThrow(() -> new ServerException("No user found for current session"));
        return listService.findAllByViewerId(userId);
    }

    @SuppressWarnings("SpringElInspection")
    @GetMapping("/{id}")
    @PreAuthorize("hasRole(T(UserRole).ADMIN) or hasPermission(#id, 'TaskList', T(Permission).READ)")
    @Transactional
    public TaskListDTO getListById(@PathVariable long id) {
        return listService
                .findById(id)
                .orElseThrow(() -> new NotFoundException("Task '" + id + "' not found"));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TaskListDTO createList() {
        Long userId = UserDetailsImpl
                .getCurrentUser()
                .map(UserDetailsImpl::getId)
                .orElseThrow(() -> new ServerException("No user found for current session"));

        TaskListDTO listDTO = new TaskListDTO();
        listDTO.setOwnerId(userId);
        return listService.create(listDTO);
    }

    @SuppressWarnings("SpringElInspection")
    @PatchMapping("/{id}")
    @PreAuthorize("hasRole(T(UserRole).ADMIN) or hasPermission(#id, 'TaskList', T(Permission).WRITE)")
    public TaskListDTO updateList(@PathVariable long id, @Valid @RequestBody TaskListDTO listDTO, BindingResult result) {
        if (result.hasErrors() && result.hasFieldErrors()) {
            throw new InvalidRequestException(
                    // Should never be null due to the hasFieldError() check above
                    Objects.requireNonNull(result.getFieldError()).getDefaultMessage());
        }
        listDTO.setId(id);
        return listService
                .update(listDTO)
                .orElseThrow(() -> new NotFoundException("List with ID '" + id + "' not found"));
    }

    @GetMapping("/{id}/share")
    @Transactional
    public List<String> getViewers(@PathVariable long id) {
        return listService
                .findById(id)
                .map(l -> l.getViewers().stream().map(UserDTO::getUsername).collect(Collectors.toList()))
                .orElseThrow(() -> new NotFoundException("List with ID '" + id + "' not found"));
    }

    @SuppressWarnings("SpringElInspection")
    @PostMapping("/{id}/share")
    @PreAuthorize("hasRole(T(UserRole).ADMIN) or hasPermission(#id, 'TaskList', T(Permission).WRITE)")
    @Transactional
    public void shareList(@PathVariable long id, @Valid @RequestBody ListShareDTO dto, BindingResult result) {
        if (result.hasErrors() && result.hasFieldErrors()) {
            throw new InvalidRequestException(
                    Objects.requireNonNull(result.getFieldError()).getDefaultMessage());
        }
        if (listService.existsByIdAndViewerUsername(id, dto.getUsername()))
            throw new InvalidRequestException("List is already shared with '" + dto.getUsername() + "'");
        if (!listService.addViewer(id, dto.getUsername()))
            throw new NotFoundException("List with ID '" + id + "' not found");
        log.debug("Shared list '" + id + "' with username '" + dto.getUsername() + "'");
    }

    @SuppressWarnings("SpringElInspection")
    @DeleteMapping("/{id}/share")
    // Read permission since we're un-sharing the list from ourselves (i.e. we're a viewer)
    @PreAuthorize("hasRole(T(UserRole).ADMIN) or hasPermission(#id, 'TaskList', T(Permission).READ)")
    @Transactional
    public void unshareList(@PathVariable long id) {
        Long userId = UserDetailsImpl.getCurrentUser()
                .map(UserDetailsImpl::getId)
                .orElseThrow(() -> new ServerException("No user found for current session"));
        if (!listService.removeViewer(id, userId))
            throw new NotFoundException("List with ID '" + id + "' not found");
        log.debug("Unshared list '" + id + "' from user '" + userId + "'");
    }

    @SuppressWarnings("SpringElInspection")
    @PostMapping("/{id}/unshare")
    @PreAuthorize("hasRole(T(UserRole).ADMIN) or hasPermission(#id, 'TaskList', T(Permission).WRITE)")
    @Transactional
    public void unshareAll(@PathVariable long id) {
        if (!listService.removeAllViewers(id))
            throw new NotFoundException("List with ID '" + id + "' not found");
        log.debug("Unshared list '" + id + "'");
    }

    @SuppressWarnings("SpringElInspection")
    @PostMapping("/{id}/complete")
    @PreAuthorize("hasRole(T(UserRole).ADMIN) or hasPermission(#id, 'TaskList', T(Permission).WRITE)")
    public void completeList(@PathVariable long id) {
        TaskDTO dto = new TaskDTO();
        dto.setState(Task.State.TASK_ARCHIVED);
        taskService.updateAllByList(id, dto);
        log.debug("Completed list '" + id + "'");
    }

    @SuppressWarnings("SpringElInspection")
    @PostMapping("/{id}/clear")
    @PreAuthorize("hasRole(T(UserRole).ADMIN) or hasPermission(#id, 'TaskList', T(Permission).WRITE)")
    public void clearList(@PathVariable long id) {
        TaskDTO dto = new TaskDTO();
        dto.setState(Task.State.TASK_INBOX);
        taskService.updateAllByList(id, dto);
        log.debug("Cleared list '" + id + "'");
    }

    @DeleteMapping
    @Transactional
    public void deleteAllLists() {
        Long userId = UserDetailsImpl.getCurrentUser()
                .map(UserDetailsImpl::getId)
                .orElseThrow(() -> new ServerException("No user found for current session"));
        listService.deleteAllByOwnerId(userId);
        log.debug("Deleted all lists of user '" + userId + "'");
    }

    @SuppressWarnings("SpringElInspection")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole(T(UserRole).ADMIN) or hasPermission(#id, 'TaskList', T(Permission).WRITE)")
    public void deleteList(@PathVariable long id) {
        if (!listService.deleteById(id)) {
            throw new NotFoundException("List with ID '" + id + "' not found");
        }
    }

    @SuppressWarnings("SpringElInspection")
    @PostMapping("/{id}/tasks")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole(T(UserRole).ADMIN) or hasPermission(#id, 'TaskList', T(Permission).WRITE)")
    public TaskDTO createTask(@PathVariable long id) {
        TaskDTO taskDTO = new TaskDTO();
        //Use a listId mapping instead of sending it in the request to allow permission checking.
        taskDTO.setListId(id);
        // Default to INBOX. new TaskDTO() doesn't set a default since PATCH /tasks might not include a state
        taskDTO.setState(Task.State.TASK_INBOX);
        return taskService.create(taskDTO);
    }
}
