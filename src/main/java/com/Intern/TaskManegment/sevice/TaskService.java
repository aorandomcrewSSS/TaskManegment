package com.Intern.TaskManegment.sevice;

import com.Intern.TaskManegment.dto.mapper.CommentMapper;
import com.Intern.TaskManegment.dto.mapper.TaskMapper;
import com.Intern.TaskManegment.dto.request.TaskCreateRequest;
import com.Intern.TaskManegment.dto.request.TaskUpdateRequest;
import com.Intern.TaskManegment.dto.response.TaskResponse;
import com.Intern.TaskManegment.model.Task;
import com.Intern.TaskManegment.model.User;
import com.Intern.TaskManegment.model.enums.Role;
import com.Intern.TaskManegment.repository.TaskRepository;
import com.Intern.TaskManegment.repository.UserRepository;
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
    public TaskResponse createTask(TaskCreateRequest taskCreateRequest, User author) {
        // Находим исполнителя по ID из запроса
        User executor = userRepository.findById(taskCreateRequest.getExecutorId())
                .orElseThrow(() -> new EntityNotFoundException("Executor not found"));

        // Маппим данные из DTO в сущность Task
        Task task = taskMapper.taskCreateRequestToTask(taskCreateRequest, author, executor);

        // Сохраняем задачу
        Task savedTask = taskRepository.save(task);
        return taskMapper.taskToTaskResponse(savedTask);  // Возвращаем TaskResponse
    }

    // Обновление задачи
    public TaskResponse updateTask(Long taskId, TaskUpdateRequest taskUpdateRequest, User user) throws AccessDeniedException {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new EntityNotFoundException("Task not found"));

        // Логика проверки доступа
        if (!task.getAuthor().equals(user) && user.getRole() != Role.ADMIN) {
            throw new AccessDeniedException("You do not have permission to update this task");
        }

        // Находим исполнителя, если он указан в обновлении
        User executor = userRepository.findById(taskUpdateRequest.getExecutorId())
                .orElseThrow(() -> new EntityNotFoundException("Executor not found"));

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

    // Получение всех задач автора с пагинацией
    public Page<TaskResponse> getTasksByAuthor(Long authorId, Pageable pageable) {
        Page<Task> tasks = taskRepository.findByAuthorId(authorId, pageable);
        return tasks.map(taskMapper::taskToTaskResponse);
    }

    // Получение всех задач исполнителя с пагинацией
    public Page<TaskResponse> getTasksByExecutor(Long executorId, Pageable pageable) {
        Page<Task> tasks = taskRepository.findByExecutorId(executorId, pageable);
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
