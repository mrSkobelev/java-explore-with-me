package ru.practicum.comment.controller;

import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.comment.dto.NewCommentDto;
import ru.practicum.comment.dto.CommentInfoDto;
import ru.practicum.comment.service.CommentService;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class PrivateCommentController {
    private final CommentService service;

    @PostMapping("/{authorId}/events/{eventId}/comments")
    @ResponseStatus(HttpStatus.CREATED)
    public CommentInfoDto createComment(
        @PathVariable Long authorId,
        @PathVariable Long eventId,
        @Valid @RequestBody NewCommentDto commentDto) {

        return service.createComment(authorId, eventId, commentDto);
    }

    @GetMapping("/{authorId}/comments")
    public List<CommentInfoDto> getAllCommentByUserId(
        @PathVariable Long authorId,
        @RequestParam(value = "from", defaultValue = "0") @Min(0) Integer from,
        @RequestParam(value = "size", defaultValue = "10") @Min(1) Integer size) {

        return service.getAllCommentByAuthorId(authorId, from, size);
    }

    @PatchMapping("/{userId}/events/{eventId}/comments/{commentId}")
    public CommentInfoDto updateComment(
        @PathVariable Long userId,
        @PathVariable Long eventId,
        @PathVariable Long commentId,
        @Valid @RequestBody NewCommentDto newCommentDto) {

        return service.updateComment(userId, eventId, commentId, newCommentDto);
    }

    @DeleteMapping("/{userId}/comments/{commentId}")
    public void deleteComment(
        @PathVariable Long userId,
        @PathVariable Long commentId) {
        service.deleteComment(userId, commentId);
    }
}
