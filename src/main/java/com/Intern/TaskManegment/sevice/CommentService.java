package com.Intern.TaskManegment.sevice;

import com.Intern.TaskManegment.dto.request.CommentCreateRequest;
import com.Intern.TaskManegment.dto.response.CommentResponse;
import com.Intern.TaskManegment.model.Comment;
import com.Intern.TaskManegment.model.Task;
import com.Intern.TaskManegment.model.User;
import com.Intern.TaskManegment.repository.CommentRepository;
import com.Intern.TaskManegment.repository.TaskRepository;
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
                .orElseThrow(() -> new EntityNotFoundException("Task not found"));

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
                .orElseThrow(() -> new EntityNotFoundException("Comment not found"));

        if (!comment.getAuthor().equals(author)) {
            throw new AccessDeniedException("You are not allowed to delete this comment");
        }

        commentRepository.delete(comment);
    }

    public List<CommentResponse> getCommentsByTask(Long taskId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new EntityNotFoundException("Task not found"));

        return task.getComments().stream()
                .map(comment -> new CommentResponse(
                        comment.getId(),
                        comment.getText(),
                        comment.getAuthor().getId(),
                        taskId))
                .collect(Collectors.toList());
    }
}