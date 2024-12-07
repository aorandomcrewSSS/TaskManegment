package com.Intern.TaskManegment.repository;

import com.Intern.TaskManegment.model.Task;
import com.Intern.TaskManegment.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<Task, Long> {
    Page<Task> findByAuthor(User author, Pageable pageable);
    Page<Task> findByExecutor(User executor, Pageable pageable);
}
