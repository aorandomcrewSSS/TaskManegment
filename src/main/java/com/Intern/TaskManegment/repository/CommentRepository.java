package com.Intern.TaskManegment.repository;

import com.Intern.TaskManegment.model.Comment;
import com.Intern.TaskManegment.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByTask(Task task);
}
