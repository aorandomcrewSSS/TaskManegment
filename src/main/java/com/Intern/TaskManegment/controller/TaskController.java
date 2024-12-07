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
import org.springframework.security.core.context.SecurityContextHolder;
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
    public ResponseEntity<TaskResponse> createTask(@RequestBody TaskCreateRequest taskCreateRequest) {
        // Извлекаем текущего пользователя из SecurityContext
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User author = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        // Передаем автора напрямую в TaskService
        TaskResponse taskResponse = taskService.createTask(taskCreateRequest, author);
        return ResponseEntity.status(HttpStatus.CREATED).body(taskResponse);
    }

    // Обновление задачи
    @PutMapping("/{taskId}")
    public ResponseEntity<TaskResponse> updateTask(
            @PathVariable Long taskId,
            @RequestBody TaskUpdateRequest taskUpdateRequest) throws AccessDeniedException {
        // Извлекаем текущего пользователя из SecurityContext
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
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
    @GetMapping("/my-tasks")
    public ResponseEntity<Page<TaskResponse>> getMyTasks(Pageable pageable) {
        // Извлекаем текущего пользователя из SecurityContext
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User author = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        Page<TaskResponse> tasks = taskService.getTasksByAuthor(author.getId(), pageable);
        return ResponseEntity.ok(tasks);
    }

    // Удаление задачи
    @DeleteMapping("/{taskId}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long taskId) throws AccessDeniedException {
        // Извлекаем текущего пользователя из SecurityContext
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        taskService.deleteTask(taskId, user);
        return ResponseEntity.noContent().build();
    }
}
