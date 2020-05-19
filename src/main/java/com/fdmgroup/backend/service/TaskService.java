package com.fdmgroup.backend.service;

import com.fdmgroup.backend.exception.InvalidRequestException;
import com.fdmgroup.backend.exception.NotFoundException;
import com.fdmgroup.backend.model.Task;
import com.fdmgroup.backend.model.TaskDTO;
import com.fdmgroup.backend.model.TaskList;
import com.fdmgroup.backend.repository.TaskListRepository;
import com.fdmgroup.backend.repository.TaskRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TaskService
        extends AbstractService<Task, TaskDTO, TaskRepository, Long>
        implements OwnedEntityService<Task, TaskDTO, Long> {

    private final TaskListRepository listRepository;

    @Autowired
    public TaskService(TaskRepository repository, TaskListRepository listRepository, ModelMapper mapper) {
        super(repository, mapper);
        this.listRepository = listRepository;
    }

    @Override
    public TaskDTO create(TaskDTO dto) {
        if (!listRepository.existsById(dto.getListId()))
            throw new InvalidRequestException("No list with ID '" + dto.getListId() + "' exists");

        dto.setCreatedAt(System.currentTimeMillis());
        return super.create(dto);
    }

    @Transactional
    public void updateAllByList(long listId, TaskDTO dto) {
        TaskList list = listRepository.findById(listId)
                .orElseThrow(() -> new NotFoundException("No list with ID '" + listId + "' exists"));
        dto.setId(null);
        dto.setListId(null);
        repository
                .findAllByList(list)
                .forEach(e -> getEntityMapper().map(dto, e));
    }

}
