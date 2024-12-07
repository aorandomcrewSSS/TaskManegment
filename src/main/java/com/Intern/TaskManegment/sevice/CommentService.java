package com.Intern.TaskManegment.sevice;

import com.Intern.TaskManegment.model.Comment;
import com.Intern.TaskManegment.model.Task;
import com.Intern.TaskManegment.model.User;
import com.Intern.TaskManegment.repository.CommentRepository;
import com.Intern.TaskManegment.repository.TaskRepository;
import com.Intern.TaskManegment.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    public Comment addComment(Long taskId, String content, String userEmail) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new EntityNotFoundException("Task not found"));

        User author = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        Comment comment = Comment.builder()
                .content(content)
                .task(task)
                .author(author)
                .build();

        return commentRepository.save(comment);
    }

    public List<Comment> getCommentsByTask(Long taskId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new EntityNotFoundException("Task not found"));
        return commentRepository.findByTask(task);
    }
}
