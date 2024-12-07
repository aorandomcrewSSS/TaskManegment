package com.Intern.TaskManegment.controller;

import com.Intern.TaskManegment.dto.request.TaskCreateRequest;
import com.Intern.TaskManegment.dto.request.TaskUpdateRequest;
import com.Intern.TaskManegment.dto.response.TaskResponse;
import com.Intern.TaskManegment.model.User;
import com.Intern.TaskManegment.repository.UserRepository;
import com.Intern.TaskManegment.sevice.TaskService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;

@RestController
@RequestMapping("/api/v1/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;
    private final UserRepository userRepository;

    // Создание задачи
    @PostMapping
    public ResponseEntity<TaskResponse> createTask(
            @RequestBody TaskCreateRequest taskCreateRequest,
            @RequestParam Long authorId) {

        User author = userRepository.findById(authorId)
                .orElseThrow(() -> new EntityNotFoundException("Author not found"));

        TaskResponse taskResponse = taskService.createTask(taskCreateRequest, author);
        return ResponseEntity.status(HttpStatus.CREATED).body(taskResponse);
    }

    // Обновление задачи
    @PutMapping("/{taskId}")
    public ResponseEntity<TaskResponse> updateTask(
            @PathVariable Long taskId,
            @RequestBody TaskUpdateRequest taskUpdateRequest,
            @RequestParam Long userId) throws AccessDeniedException {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        TaskResponse taskResponse = taskService.updateTask(taskId, taskUpdateRequest, user);
        return ResponseEntity.ok(taskResponse);
    }

    // Получение задачи по ID
    @GetMapping("/{taskId}")
    public ResponseEntity<TaskResponse> getTaskById(@PathVariable Long taskId) {
        TaskResponse taskResponse = taskService.getTaskById(taskId);
        return ResponseEntity.ok(taskResponse);
    }

    // Получение задач по автору с пагинацией
    @GetMapping("/author/{authorId}")
    public ResponseEntity<Page<TaskResponse>> getTasksByAuthor(
            @PathVariable Long authorId,
            Pageable pageable) {

        Page<TaskResponse> tasks = taskService.getTasksByAuthor(authorId, pageable);
        return ResponseEntity.ok(tasks);
    }

    // Получение задач по исполнителю с пагинацией
    @GetMapping("/executor/{executorId}")
    public ResponseEntity<Page<TaskResponse>> getTasksByExecutor(
            @PathVariable Long executorId,
            Pageable pageable) {

        Page<TaskResponse> tasks = taskService.getTasksByExecutor(executorId, pageable);
        return ResponseEntity.ok(tasks);
    }

    // Удаление задачи
    @DeleteMapping("/{taskId}")
    public ResponseEntity<Void> deleteTask(
            @PathVariable Long taskId,
            @RequestParam Long userId) throws AccessDeniedException {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        taskService.deleteTask(taskId, user);
        return ResponseEntity.noContent().build();
    }
}
