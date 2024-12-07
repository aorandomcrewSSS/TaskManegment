package com.Intern.TaskManegment.controller;

import com.Intern.TaskManegment.model.Task;
import com.Intern.TaskManegment.sevice.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;

@RestController
@RequestMapping("/api/v1/tasks")
@RequiredArgsConstructor
public class TaskController {
    private final TaskService taskService;

    @PostMapping
    public ResponseEntity<Task> createTask(@RequestBody Task task, Authentication authentication) {
        return ResponseEntity.ok(taskService.createTask(task, authentication.getName()));
    }

    @PutMapping("/{taskId}")
    public ResponseEntity<Task> updateTask(
            @PathVariable Long taskId,
            @RequestBody Task updatedTask,
            Authentication authentication) throws AccessDeniedException {
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));
        return ResponseEntity.ok(taskService.updateTask(taskId, updatedTask, authentication.getName(), isAdmin));
    }

    @DeleteMapping("/{taskId}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long taskId, Authentication authentication) throws AccessDeniedException {
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));
        taskService.deleteTask(taskId, authentication.getName(), isAdmin);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/author/{authorEmail}")
    public ResponseEntity<Page<Task>> getTasksByAuthor(
            @PathVariable String authorEmail,
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(taskService.getTasksByAuthor(authorEmail, pageable));
    }

    @GetMapping("/executor/{executorEmail}")
    public ResponseEntity<Page<Task>> getTasksByExecutor(
            @PathVariable String executorEmail,
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(taskService.getTasksByExecutor(executorEmail, pageable));
    }
}
