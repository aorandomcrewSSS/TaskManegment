package com.Intern.TaskManegment.controller;

import com.Intern.TaskManegment.dto.request.CommentCreateRequest;
import com.Intern.TaskManegment.dto.response.CommentResponse;
import com.Intern.TaskManegment.model.User;
import com.Intern.TaskManegment.repository.UserRepository;
import com.Intern.TaskManegment.sevice.CommentService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;
    private final UserRepository userRepository;

    // Добавление комментария к задаче
    @PostMapping("/task/{taskId}")
    public ResponseEntity<CommentResponse> addComment(
            @PathVariable Long taskId,
            @RequestBody CommentCreateRequest commentCreateRequest) {

        // Извлекаем текущего пользователя из SecurityContext
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User author = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        // Передаем автора в CommentService
        CommentResponse commentResponse = commentService.addComment(taskId, author, commentCreateRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(commentResponse);
    }

    // Получение комментариев по задаче
    @GetMapping("/task/{taskId}")
    public ResponseEntity<List<CommentResponse>> getCommentsByTask(@PathVariable Long taskId) {
        List<CommentResponse> commentResponses = commentService.getCommentsByTask(taskId);
        return ResponseEntity.ok(commentResponses);
    }

    // Удаление комментария
    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long commentId) throws AccessDeniedException {
        // Извлекаем текущего пользователя из SecurityContext
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User author = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        // Передаем автора в CommentService
        commentService.deleteComment(commentId, author);
        return ResponseEntity.noContent().build();
    }
}