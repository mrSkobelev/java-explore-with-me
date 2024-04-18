package ru.practicum.comment.repository;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.comment.model.Comment;
import ru.practicum.event.model.Event;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    Page<Comment> findAllByAuthor_Id(Long userId, PageRequest pageRequest);

    List<Comment> findAllByEvent_Id(Long eventId);

    List<Comment> findByEventIn(List<Event> events, Sort sort);
}
