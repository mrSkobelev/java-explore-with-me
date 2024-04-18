package ru.practicum.event.repository;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.EventState;

public interface EventRepository extends JpaRepository<Event, Long>, QuerydslPredicateExecutor<Event> {
    Page<Event> findByInitiatorId(long initiatorId, PageRequest pageRequest);

    Optional<Event> findByIdAndInitiator_Id(Long eventId, Long initiatorId);

    Optional<Event> findByIdAndStateEquals(Long eventId, EventState state);
}
