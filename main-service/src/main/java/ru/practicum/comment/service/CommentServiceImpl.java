package ru.practicum.comment.service;

import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import ru.practicum.comment.dto.CommentInfoDto;
import ru.practicum.comment.dto.CommentMapper;
import ru.practicum.comment.dto.NewCommentDto;
import ru.practicum.comment.model.Comment;
import ru.practicum.comment.repository.CommentRepository;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.EventState;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;

@Service
@Slf4j
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    @Override
    public CommentInfoDto createComment(Long userId, Long eventId, NewCommentDto newCommentDto) {
        log.info("Создать комментарий пользователем с id: {} для события с id: {}", userId, eventId);

        User author = validUser(userId);
        Event event = validEvent(eventId);

        if (event.getState() != null && !event.getState().equals(EventState.PUBLISHED)) {
            throw new ConflictException("Событие ещё не опубликовано");
        }

        Comment comment = CommentMapper.toComment(newCommentDto, event, author);
        Comment savedComment = commentRepository.save(comment);

        log.info("Создан комментарий пользователем с id: {} для события с id: {}", userId, eventId);

        return CommentMapper.toCommentInfoDto(savedComment);
    }

    @Override
    public List<CommentInfoDto> getAllCommentByAuthorId(Long authorId, Integer from, Integer size) {
        log.info("Получить все комментарии пользователя с id: {}", authorId);

        validUser(authorId);

        Sort sort = Sort.by(Direction.DESC, "created");
        PageRequest pageRequest = PageRequest.of(from / size, size, sort);

        List<Comment> comments = commentRepository.findAllByAuthor_Id(authorId, pageRequest).getContent();

        log.info("Получены все комментарии пользователя с id: {}", authorId);

        return comments.stream().map(CommentMapper::toCommentInfoDto).collect(Collectors.toList());
    }

    @Override
    public CommentInfoDto updateComment(Long authorId, Long eventId, Long commentId, NewCommentDto newCommentDto) {
        log.info("Редактировать комментарий с id: {} пользователем с id: {}", commentId, authorId);

        validUser(authorId);
        validEvent(eventId);
        Comment comment = validComment(commentId);

        if (comment.getAuthor().getId() != authorId) {
            throw new ConflictException("Только автор комментария может его редактировать");
        }

        comment.setText(newCommentDto.getText());
        Comment savedComment = commentRepository.save(comment);

        log.info("Успешно отредактирован комментарий с id: {} автором с id: {}", commentId, authorId);

        return CommentMapper.toCommentInfoDto(savedComment);
    }

    @Override
    public void deleteComment(Long authorId, Long commentId) {
        log.info("Удалить комментарий с id: {} пользователем с id: {}", commentId, authorId);

        validUser(authorId);
        Comment comment = validComment(commentId);

        if (comment.getAuthor().getId() != authorId) {
            throw new ConflictException("Только автор может удалять комментарий");
        }

        commentRepository.deleteById(commentId);

        log.info("Успешно удален комментарий с id: {} автором с id: {}", commentId, authorId);
    }

    @Override
    public void adminDeleteComment(Long eventId, Long commentId) {
        log.info("Удаление админом комментария с id: {}", commentId);

        validEvent(eventId);
        validComment(commentId);

        commentRepository.deleteById(commentId);

        log.info("Успешно удален админом комментарий с id: {}", commentId);
    }

    private User validUser(Long userId) {
        return userRepository.findById(userId).orElseThrow(
            () -> new NotFoundException("Не найден пользователь с id: " + userId)
        );
    }

    private Event validEvent(Long eventId) {
        return eventRepository.findById(eventId).orElseThrow(
            () -> new NotFoundException("Не найдено событие с id: " + eventId)
        );
    }

    private Comment validComment(Long commentId) {
        return commentRepository.findById(commentId).orElseThrow(
            () -> new NotFoundException("Не найден комментарий с id: " + commentId)
        );
    }
}
