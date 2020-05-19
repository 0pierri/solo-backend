package com.fdmgroup.backend.service;

import com.fdmgroup.backend.model.TaskList;
import com.fdmgroup.backend.model.TaskListDTO;
import com.fdmgroup.backend.repository.TaskListRepository;
import com.fdmgroup.backend.repository.UserRepository;
import lombok.Getter;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Getter
@Service
public class ListService
        extends AbstractService<TaskList, TaskListDTO, TaskListRepository, Long>
        implements OwnedEntityService<TaskList, TaskListDTO, Long>,
                   ViewableEntityService<TaskList, TaskListDTO, Long> {

    private final UserRepository userRepository;

    public ListService(TaskListRepository repository, UserRepository userRepository, ModelMapper mapper) {
        super(repository, mapper);
        this.userRepository = userRepository;
    }
}
