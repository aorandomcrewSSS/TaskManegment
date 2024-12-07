package com.Intern.TaskManegment.controller;

import com.Intern.TaskManegment.dto.request.CommentCreateRequest;
import com.Intern.TaskManegment.dto.response.CommentResponse;
import com.Intern.TaskManegment.sevice.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    // Добавление комментария к задаче
    @PostMapping("/task/{taskId}")
    public ResponseEntity<CommentResponse> addComment(
            @PathVariable Long taskId,
            @RequestParam Long userId,
            @RequestBody CommentCreateRequest commentCreateRequest) {

        CommentResponse commentResponse = commentService.addComment(taskId, userId, commentCreateRequest);
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
    public ResponseEntity<Void> deleteComment(
            @PathVariable Long commentId,
            @RequestParam Long userId) throws AccessDeniedException {

        commentService.deleteComment(commentId, userId);
        return ResponseEntity.noContent().build();
    }
}