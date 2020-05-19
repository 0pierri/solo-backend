package com.fdmgroup.backend.repository;

import com.fdmgroup.backend.model.Task;
import com.fdmgroup.backend.model.TaskList;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskRepository extends OwnedEntityRepository<Task, Long> {

    /**
     * Use {@link TaskRepository#findAllByOwner_Id(Long)} instead
     */
    @SuppressWarnings("SpringDataRepositoryMethodParametersInspection")
    Iterable<Task> findAllByList_Owner_Id(Long ownerId);

    default Iterable<Task> findAllByOwner_Id(Long ownedId) {
        return findAllByList_Owner_Id(ownedId);
    }

    /**
     * Use {@link TaskRepository#existsByIdAndOwner_Id(Long, Long)} instead
     */
    @SuppressWarnings("SpringDataRepositoryMethodParametersInspection")
    boolean existsByIdAndList_Owner_Id(Long id, Long ownerId);

    default boolean existsByIdAndOwner_Id(Long id, Long ownerId) {
        return existsByIdAndList_Owner_Id(id, ownerId);
    }

    /**
     * Use {@link TaskRepository#deleteAllByOwner_Id(Long)} instead
     */
    @SuppressWarnings("SpringDataRepositoryMethodParametersInspection")
    void deleteAllByList_Owner_Id(Long ownerId);

    default void deleteAllByOwner_Id(Long ownerId) {
        deleteAllByList_Owner_Id(ownerId);
    }

    /**
     * @param list a {@code TaskList}
     * @return all tasks that belong to the specified list
     */
    Iterable<Task> findAllByList(TaskList list);

}
