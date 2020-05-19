package com.fdmgroup.backend.repository;

import com.fdmgroup.backend.model.TaskList;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskListRepository extends OwnedEntityRepository<TaskList, Long>,
                                            ViewableEntityRepository<TaskList, Long>{
}
