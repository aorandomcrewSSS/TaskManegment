package com.Intern.TaskManegment.sevice;

import com.Intern.TaskManegment.dto.mapper.TaskMapper;
import com.Intern.TaskManegment.dto.request.TaskCreateRequest;
import com.Intern.TaskManegment.dto.request.TaskUpdateRequest;
import com.Intern.TaskManegment.dto.response.TaskResponse;
import com.Intern.TaskManegment.model.Task;
import com.Intern.TaskManegment.model.User;
import com.Intern.TaskManegment.model.enums.Priority;
import com.Intern.TaskManegment.model.enums.Role;
import com.Intern.TaskManegment.model.enums.Status;
import com.Intern.TaskManegment.repository.TaskRepository;
import com.Intern.TaskManegment.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TaskMapper taskMapper;

    @InjectMocks
    private TaskService taskService;

    @Test
    void createTask_Success() {
        // Arrange
        User author = new User(1L, "Author", "author@example.com", "password", Role.USER, List.of());
        User executor = new User(2L, "Executor", "executor@example.com", "password", Role.USER, List.of());
        TaskCreateRequest request = new TaskCreateRequest("Task Title", "Task Description", "PENDING", "HIGH", 2L);
        Task task = new Task(1L, "Task Title", "Task Description", Status.PENDING, Priority.HIGH, author, executor, List.of());
        TaskResponse response = new TaskResponse(1L, "Task Title", "Task Description", "PENDING", "HIGH", 1L, 2L, List.of());

        when(userRepository.findById(2L)).thenReturn(Optional.of(executor));
        when(taskMapper.taskCreateRequestToTask(request, author, executor)).thenReturn(task);
        when(taskRepository.save(task)).thenReturn(task);
        when(taskMapper.taskToTaskResponse(task)).thenReturn(response);

        // Act
        TaskResponse result = taskService.createTask(request, author);

        // Assert
        assertEquals(response, result);
        verify(userRepository).findById(2L);
        verify(taskMapper).taskCreateRequestToTask(request, author, executor);
        verify(taskRepository).save(task);
    }

    @Test
    void createTask_ExecutorNotFound() {
        // Arrange
        User author = new User(1L, "Author", "author@example.com", "password", Role.USER, List.of());
        TaskCreateRequest request = new TaskCreateRequest("Task Title", "Task Description", "PENDING", "HIGH", 2L);

        when(userRepository.findById(2L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> taskService.createTask(request, author));
        verify(userRepository).findById(2L);
        verifyNoInteractions(taskMapper, taskRepository);
    }

    @Test
    void updateTask_AccessDenied() {
        // Arrange
        User user = new User(1L, "User", "user@example.com", "password", Role.USER, List.of());
        Task task = new Task(1L, "Task Title", "Task Description", Status.PENDING, Priority.HIGH, new User(2L, "Another Author", "author@example.com", "password", Role.USER, List.of()), null, List.of());
        TaskUpdateRequest request = new TaskUpdateRequest("IN_PROGRESS", "MEDIUM", null);

        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));

        // Act & Assert
        assertThrows(AccessDeniedException.class, () -> taskService.updateTask(1L, request, user));
        verify(taskRepository).findById(1L);
        verifyNoInteractions(userRepository, taskMapper);
    }

    @Test
    void deleteTask_Success() throws AccessDeniedException {
        // Arrange
        User admin = new User(1L, "Admin", "admin@example.com", "password", Role.ADMIN, List.of());
        Task task = new Task(1L, "Task Title", "Task Description", Status.PENDING, Priority.HIGH, admin, null, List.of());

        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));

        // Act
        taskService.deleteTask(1L, admin);

        // Assert
        verify(taskRepository).findById(1L);
        verify(taskRepository).delete(task);
    }
}