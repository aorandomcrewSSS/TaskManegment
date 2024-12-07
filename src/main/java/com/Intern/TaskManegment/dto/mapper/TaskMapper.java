package com.Intern.TaskManegment.dto.mapper;

import com.Intern.TaskManegment.dto.request.TaskCreateRequest;
import com.Intern.TaskManegment.dto.request.TaskUpdateRequest;
import com.Intern.TaskManegment.dto.response.CommentResponse;
import com.Intern.TaskManegment.dto.response.TaskResponse;
import com.Intern.TaskManegment.model.Task;
import com.Intern.TaskManegment.model.User;
import com.Intern.TaskManegment.model.enums.Priority;
import com.Intern.TaskManegment.model.enums.Status;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class TaskMapper {

    public Task taskCreateRequestToTask(TaskCreateRequest taskCreateRequest, User author, User executor) {
        Task task = new Task();
        task.setTitle(taskCreateRequest.getTitle());
        task.setDescription(taskCreateRequest.getDescription());
        task.setStatus(Status.valueOf(taskCreateRequest.getStatus()));  // Преобразуем строку в Enum
        task.setPriority(Priority.valueOf(taskCreateRequest.getPriority()));  // Преобразуем строку в Enum
        task.setAuthor(author);
        task.setExecutor(executor);
        task.setComments(new ArrayList<>());  // Игнорируем комментарии
        return task;
    }

    public Task taskUpdateRequestToTask(TaskUpdateRequest taskUpdateRequest, Task existingTask, User executor) {
        existingTask.setStatus(Status.valueOf(taskUpdateRequest.getStatus()));  // Преобразуем строку в Enum
        existingTask.setPriority(Priority.valueOf(taskUpdateRequest.getPriority()));  // Преобразуем строку в Enum
        existingTask.setExecutor(executor);
        return existingTask;
    }

    public TaskResponse taskToTaskResponse(Task task) {
        TaskResponse response = new TaskResponse();
        response.setId(task.getId());
        response.setTitle(task.getTitle());
        response.setDescription(task.getDescription());
        response.setStatus(task.getStatus().name());  // Преобразуем Enum в строку
        response.setPriority(task.getPriority().name());  // Преобразуем Enum в строку
        response.setAuthorId(task.getAuthor().getId());
        response.setExecutorId(task.getExecutor() != null ? task.getExecutor().getId() : null);
        // Преобразуем комментарии
        List<CommentResponse> commentResponses = task.getComments().stream()
                .map(comment -> new CommentResponse(
                        comment.getId(),
                        comment.getText(),
                        comment.getAuthor().getId(),
                        comment.getTask().getId()))
                .collect(Collectors.toList());
        response.setComments(commentResponses);
        return response;
    }
}
