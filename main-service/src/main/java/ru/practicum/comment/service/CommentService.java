package ru.practicum.comment.service;

import java.util.List;
import ru.practicum.comment.dto.NewCommentDto;
import ru.practicum.comment.dto.CommentInfoDto;

public interface CommentService {
    CommentInfoDto createComment(Long authorId, Long eventId, NewCommentDto newCommentDto);

    List<CommentInfoDto> getAllCommentByAuthorId(Long authorId, Integer from, Integer size);

    CommentInfoDto updateComment(Long authorId, Long eventId, Long commentId, NewCommentDto newCommentDto);

    void deleteComment(Long authorId, Long commentId);

    void adminDeleteComment(Long eventId, Long commentId);
}
