package com.fdmgroup.backend.security;

import com.fdmgroup.backend.model.Task;
import com.fdmgroup.backend.model.TaskList;
import com.fdmgroup.backend.repository.TaskListRepository;
import com.fdmgroup.backend.repository.TaskRepository;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;

@Component
public class CustomEntityPermissionEvaluator implements PermissionEvaluator {

    // Lazily get repository beans to avoid startup issues
    private final ObjectFactory<TaskListRepository> taskListRepoFactory;
    private final ObjectFactory<TaskRepository> taskRepoFactory;

    public CustomEntityPermissionEvaluator(ObjectFactory<TaskListRepository> taskListRepoFactory,
                                           ObjectFactory<TaskRepository> taskRepoFactory) {
        this.taskListRepoFactory = taskListRepoFactory;
        this.taskRepoFactory = taskRepoFactory;
    }

    @Override
    @Transactional
    public boolean hasPermission(Authentication authentication, Object target, Object permission) {
        if (authentication == null || !(authentication.getPrincipal() instanceof UserDetailsImpl)) {
            return false;
        }

        var user = (UserDetailsImpl)(authentication.getPrincipal());

        //TODO: maybe generify this

        if (target instanceof TaskList) {
            var repo = taskListRepoFactory.getObject();
            if (repo.existsByIdAndOwner_Id(((TaskList) target).getId(), user.getId()))
                return true;
            return permission.equals(Permission.READ) && repo.existsByIdAndViewers_Id(((TaskList) target).getId(), user.getId());
        }
        if (target instanceof Task) {
            return (((Task) target).getOwner().getId().equals(user.getId()));
        }

        return false;
    }

    @Override
    public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType, Object permission) {
        if (authentication == null
                || !(authentication.getPrincipal() instanceof UserDetailsImpl)
                || !(targetId instanceof Long)) {
            return false;
        }

        var userId = ((UserDetailsImpl)(authentication.getPrincipal())).getId();
        var entityId = (Long) targetId;

        switch(targetType) {
            case "TaskList":
                var taskListRepo = taskListRepoFactory.getObject();
                if (taskListRepo.existsByIdAndOwner_Id(entityId, userId))
                    return true;
                return permission.equals(Permission.READ) && taskListRepo.existsByIdAndViewers_Id(entityId, userId);
            case "Task":
                var taskRepo = taskRepoFactory.getObject();
                return taskRepo.existsByIdAndOwner_Id(entityId, userId);
            default:
                return false;
        }
    }
}
