package com.Intern.TaskManagement.service;

import com.Intern.TaskManagement.dto.mapper.TaskMapper;
import com.Intern.TaskManagement.dto.request.TaskCreateRequest;
import com.Intern.TaskManagement.dto.request.TaskUpdateRequest;
import com.Intern.TaskManagement.dto.response.TaskResponse;
import com.Intern.TaskManagement.model.Task;
import com.Intern.TaskManagement.model.User;
import com.Intern.TaskManagement.model.enums.Role;
import com.Intern.TaskManagement.repository.TaskRepository;
import com.Intern.TaskManagement.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.nio.file.AccessDeniedException;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final TaskMapper taskMapper;

    // Создание задачи
    public TaskResponse createTask(TaskCreateRequest taskCreateRequest, User author) throws AccessDeniedException {
        User executor = null;
        if (taskCreateRequest.getExecutorId() != null) {
            executor = userRepository.findById(taskCreateRequest.getExecutorId())
                    .orElseThrow(() -> new EntityNotFoundException("Executor not found"));
        }

        if (author.getRole() != Role.ADMIN) {
            throw new AccessDeniedException("Только администратор может создать задачу");
        }

        Task task = taskMapper.taskCreateRequestToTask(taskCreateRequest, author, executor);
        taskRepository.save(task);

        return taskMapper.taskToTaskResponse(task);
    }

    // Обновление задачи
    public TaskResponse updateTask(Long taskId, TaskUpdateRequest taskUpdateRequest, User user) throws AccessDeniedException {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new EntityNotFoundException("Task not found"));

        // Логика проверки доступа должна идти до поиска исполнителя
        if (!task.getAuthor().equals(user) && user.getRole() != Role.ADMIN) {
            throw new AccessDeniedException("You do not have permission to update this task");
        }

        // Если все проверки прошли, то выполняем поиск исполнителя
        User executor = null;
        if (taskUpdateRequest.getExecutorId() != null) {
            executor = userRepository.findById(taskUpdateRequest.getExecutorId())
                    .orElseThrow(() -> new EntityNotFoundException("Executor not found"));
        }

        // Маппим обновленные данные из DTO
        task = taskMapper.taskUpdateRequestToTask(taskUpdateRequest, task, executor);

        // Сохраняем обновленную задачу
        Task updatedTask = taskRepository.save(task);
        return taskMapper.taskToTaskResponse(updatedTask);  // Возвращаем TaskResponse
    }

    // Получение задачи по ID
    public TaskResponse getTaskById(Long taskId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new EntityNotFoundException("Task not found"));

        return taskMapper.taskToTaskResponse(task); // Возвращаем TaskResponse
    }

    // Получение всех задач исполнителя с пагинацией
    public Page<TaskResponse> getTasksByExecutor(Long executorId, Pageable pageable) {
        Page<Task> tasks = taskRepository.findByExecutorId(executorId, pageable);
        return tasks.map(taskMapper::taskToTaskResponse);
    }

    public Page<TaskResponse> getTasksByAuthor(Long authorId, Pageable pageable) {
        Page<Task> tasks = taskRepository.findByAuthorId(authorId, pageable);
        return tasks.map(taskMapper::taskToTaskResponse);
    }

    // Удаление задачи
    public void deleteTask(Long taskId, User user) throws AccessDeniedException {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new EntityNotFoundException("Task not found"));

        // Проверяем, что пользователь может удалять задачу
        if (!task.getAuthor().equals(user) && user.getRole() != Role.ADMIN) {
            throw new AccessDeniedException("You do not have permission to delete this task");
        }

        taskRepository.delete(task); // Удаляем задачу
    }
}
