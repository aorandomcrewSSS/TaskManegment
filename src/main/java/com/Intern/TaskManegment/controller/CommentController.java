package com.Intern.TaskManegment.controller;

import com.Intern.TaskManegment.model.Comment;
import com.Intern.TaskManegment.sevice.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/comments")
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;

    @PostMapping("/{taskId}")
    public ResponseEntity<Comment> addComment(
            @PathVariable Long taskId,
            @RequestBody String content,
            Principal principal) {
        return ResponseEntity.ok(commentService.addComment(taskId, content, principal.getName()));
    }

    @GetMapping("/{taskId}")
    public ResponseEntity<List<Comment>> getCommentsByTask(@PathVariable Long taskId) {
        return ResponseEntity.ok(commentService.getCommentsByTask(taskId));
    }
}
