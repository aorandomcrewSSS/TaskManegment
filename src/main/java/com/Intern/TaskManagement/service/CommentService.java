package com.Intern.TaskManagement.service;

import com.Intern.TaskManagement.dto.request.CommentCreateRequest;
import com.Intern.TaskManagement.dto.response.CommentResponse;
import com.Intern.TaskManagement.model.Comment;
import com.Intern.TaskManagement.model.Task;
import com.Intern.TaskManagement.model.User;
import com.Intern.TaskManagement.repository.CommentRepository;
import com.Intern.TaskManagement.repository.TaskRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final TaskRepository taskRepository;

    public CommentResponse addComment(Long taskId, User author, CommentCreateRequest commentCreateRequest) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new EntityNotFoundException("Задача не найдена"));

        Comment comment = new Comment();
        comment.setTask(task);
        comment.setAuthor(author);
        comment.setText(commentCreateRequest.getText());
        commentRepository.save(comment);

        return new CommentResponse(
                comment.getId(),
                comment.getText(),
                author.getId(),
                task.getId()
        );
    }

    public void deleteComment(Long commentId, User author) throws AccessDeniedException {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("Комментарий не найден"));

        if (!comment.getAuthor().equals(author)) {
            throw new AccessDeniedException("только автор может удалить комментарий");
        }

        commentRepository.delete(comment);
    }

    public List<CommentResponse> getCommentsByTask(Long taskId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new EntityNotFoundException("Задача не найдена"));

        return task.getComments().stream()
                .map(comment -> new CommentResponse(
                        comment.getId(),
                        comment.getText(),
                        comment.getAuthor().getId(),
                        taskId))
                .collect(Collectors.toList());
    }
}