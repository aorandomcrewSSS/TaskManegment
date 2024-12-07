package com.Intern.TaskManegment.sevice;

import com.Intern.TaskManegment.model.Comment;
import com.Intern.TaskManegment.model.Task;
import com.Intern.TaskManegment.model.User;
import com.Intern.TaskManegment.model.enums.Status;
import com.Intern.TaskManegment.repository.CommentRepository;
import com.Intern.TaskManegment.repository.TaskRepository;
import com.Intern.TaskManegment.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.nio.file.AccessDeniedException;

@Service
@RequiredArgsConstructor
public class TaskService {
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    public Task createTask(Task task, String authorEmail) {
        User author = userRepository.findByEmail(authorEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        task.setAuthor(author);
        task.setStatus(Status.PENDING);
        return taskRepository.save(task);
    }

    public Task updateTask(Long taskId, Task updatedTask, String userEmail, boolean isAdmin) throws AccessDeniedException {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new EntityNotFoundException("Task not found"));

        if (!isAdmin && !task.getAuthor().getEmail().equals(userEmail)) {
            throw new AccessDeniedException("Unauthorized to edit this task");
        }

        task.setTitle(updatedTask.getTitle());
        task.setDescription(updatedTask.getDescription());
        task.setPriority(updatedTask.getPriority());
        task.setStatus(updatedTask.getStatus());
        if (updatedTask.getExecutor() != null) {
            User executor = userRepository.findById(updatedTask.getExecutor().getId())
                    .orElseThrow(() -> new EntityNotFoundException("Executor not found"));
            task.setExecutor(executor);
        }

        return taskRepository.save(task);
    }

    public void deleteTask(Long taskId, String userEmail, boolean isAdmin) throws AccessDeniedException {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new EntityNotFoundException("Task not found"));

        if (!isAdmin && !task.getAuthor().getEmail().equals(userEmail)) {
            throw new AccessDeniedException("Unauthorized to delete this task");
        }

        taskRepository.delete(task);
    }

    public Page<Task> getTasksByAuthor(String authorEmail, Pageable pageable) {
        User author = userRepository.findByEmail(authorEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return taskRepository.findByAuthor(author, pageable);
    }

    public Page<Task> getTasksByExecutor(String executorEmail, Pageable pageable) {
        User executor = userRepository.findByEmail(executorEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return taskRepository.findByExecutor(executor, pageable);
    }
}
