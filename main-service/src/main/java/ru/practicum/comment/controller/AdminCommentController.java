package ru.practicum.comment.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.comment.service.CommentService;

@RestController
@RequestMapping("/admin/events")
@RequiredArgsConstructor
public class AdminCommentController {
    private final CommentService service;

    @DeleteMapping("/{eventId}/comments/{commentId}")
    public void adminDeleteComment(@PathVariable Long eventId, @PathVariable Long commentId) {
        service.adminDeleteComment(eventId, commentId);
    }
}
