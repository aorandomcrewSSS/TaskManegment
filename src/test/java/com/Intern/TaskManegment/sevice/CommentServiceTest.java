package com.Intern.TaskManegment.sevice;

import com.Intern.TaskManegment.dto.request.CommentCreateRequest;
import com.Intern.TaskManegment.dto.response.CommentResponse;
import com.Intern.TaskManegment.model.Comment;
import com.Intern.TaskManegment.model.Task;
import com.Intern.TaskManegment.model.User;
import com.Intern.TaskManegment.model.enums.Priority;
import com.Intern.TaskManegment.model.enums.Role;
import com.Intern.TaskManegment.model.enums.Status;
import com.Intern.TaskManegment.repository.CommentRepository;
import com.Intern.TaskManegment.repository.TaskRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private CommentService commentService;

    @Test
    void addComment_Success() {

        User author = new User(1L, "Author", "author@example.com", "password", Role.USER, List.of());
        Task task = new Task(1L, "Task Title", "Task Description", Status.PENDING, Priority.HIGH, author, null, List.of());
        CommentCreateRequest request = new CommentCreateRequest("New comment");

        Comment comment = new Comment();
        comment.setId(1L);
        comment.setText("New comment");
        comment.setAuthor(author);
        comment.setTask(task);

        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(commentRepository.save(Mockito.any(Comment.class))).thenAnswer(invocation -> {
            Comment savedComment = invocation.getArgument(0);
            savedComment.setId(1L);
            return savedComment;
        });


        CommentResponse result = commentService.addComment(1L, author, request);


        assertEquals(1L, result.getId());
        assertEquals("New comment", result.getText());
        assertEquals(1L, result.getAuthorId());
        assertEquals(1L, result.getTaskId());
        verify(taskRepository).findById(1L);
        verify(commentRepository).save(Mockito.any(Comment.class));
    }

    @Test
    void deleteComment_AccessDenied() {

        User author = new User(1L, "Author", "author@example.com", "password", Role.USER, List.of());
        User otherUser = new User(2L, "Other User", "other@example.com", "password", Role.USER, List.of());
        Comment comment = new Comment(1L, null, author, "Comment Text");

        when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));

        assertThrows(AccessDeniedException.class, () -> commentService.deleteComment(1L, otherUser));
        verify(commentRepository).findById(1L);
    }
}