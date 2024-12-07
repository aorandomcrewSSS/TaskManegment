package com.Intern.TaskManegment.repository;

import com.Intern.TaskManegment.model.Task;
import com.Intern.TaskManegment.model.User;
import com.Intern.TaskManegment.model.enums.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<Task, Long> {
    Page<Task> findByAuthorId(Long authorId, Pageable pageable);
    Page<Task> findByExecutorId(Long executorId, Pageable pageable);
    Page<Task> findByStatus(Status status, Pageable pageable);
}
