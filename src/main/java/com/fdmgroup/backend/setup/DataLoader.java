package com.fdmgroup.backend.setup;

import com.fdmgroup.backend.model.Task;
import com.fdmgroup.backend.model.TaskList;
import com.fdmgroup.backend.model.User;
import com.fdmgroup.backend.repository.TaskListRepository;
import com.fdmgroup.backend.repository.TaskRepository;
import com.fdmgroup.backend.repository.UserRepository;
import com.fdmgroup.backend.security.UserRole;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Log4j2
@Component
public class DataLoader implements ApplicationRunner {

    private final TaskListRepository taskListRepository;
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Autowired
    public DataLoader(TaskListRepository listRepository,
                      TaskRepository taskRepository,
                      UserRepository userRepository,
                      BCryptPasswordEncoder passwordEncoder) {
        this.taskListRepository = listRepository;
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (taskListRepository.count() > 0) return;

        User user = new User("user@email.com", "user", passwordEncoder.encode("password"), UserRole.Role.ROLE_USER, List.of(), Set.of());
        user = userRepository.save(user);
        User user2 = new User("user2@email.com", "user2", passwordEncoder.encode("password"),UserRole.Role.ROLE_USER, List.of(), Set.of());
        userRepository.save(user2);
        User admin = new User("admin@email.com", "admin", passwordEncoder.encode("password"), UserRole.Role.ROLE_ADMIN, List.of(), Set.of());
        userRepository.save(admin);

        log.info("Loading initial data");
        TaskList listA = taskListRepository.save(new TaskList("List A", user));
        var tasks = new ArrayList<Task>();
        for (int i = 0; i < 5; i++) {
            tasks.add(new Task("Task " + i, listA));
        }
        taskRepository.saveAll(tasks);

        TaskList listB = taskListRepository.save(new TaskList("List B", user));
        tasks.clear();
        Task t = new Task("Task Pinned", listB);
        t.setState(Task.State.TASK_PINNED);
        taskRepository.save(t);
        t = new Task("Task Done", listB);
        t.setState(Task.State.TASK_ARCHIVED);
        taskRepository.save(t);

        log.info("Done");
    }
}
