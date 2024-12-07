package com.Intern.TaskManegment.sevice;

import com.Intern.TaskManegment.dto.mapper.CommentMapper;
import com.Intern.TaskManegment.dto.request.CommentCreateRequest;
import com.Intern.TaskManegment.dto.response.CommentResponse;
import com.Intern.TaskManegment.model.Comment;
import com.Intern.TaskManegment.model.Task;
import com.Intern.TaskManegment.model.User;
import com.Intern.TaskManegment.repository.CommentRepository;
import com.Intern.TaskManegment.repository.TaskRepository;
import com.Intern.TaskManegment.repository.UserRepository;
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
    private final UserRepository userRepository;
    private final CommentMapper commentMapper;

    // Добавление комментария к задаче
    public CommentResponse addComment(Long taskId, Long userId, CommentCreateRequest commentCreateRequest) {
        // Находим задачу и пользователя
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new EntityNotFoundException("Task not found"));
        User author = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        // Маппим данные из DTO в сущность
        Comment comment = commentMapper.commentCreateRequestToComment(commentCreateRequest, task, author);

        // Сохраняем комментарий
        Comment savedComment = commentRepository.save(comment);
        return commentMapper.commentToCommentResponse(savedComment);
    }

    // Получение комментариев по задаче
    public List<CommentResponse> getCommentsByTask(Long taskId) {
        // Получаем все комментарии для задачи
        List<Comment> comments = commentRepository.findByTaskId(taskId);
        return comments.stream()
                .map(commentMapper::commentToCommentResponse)
                .collect(Collectors.toList());
    }

    // Удаление комментария
    public void deleteComment(Long commentId, Long userId) throws AccessDeniedException {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("Comment not found"));
        User author = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        // Проверяем, что пользователь может удалить комментарий (например, если это автор комментария)
        if (!comment.getAuthor().equals(author)) {
            throw new AccessDeniedException("You do not have permission to delete this comment");
        }

        // Удаляем комментарий
        commentRepository.delete(comment);
    }
}