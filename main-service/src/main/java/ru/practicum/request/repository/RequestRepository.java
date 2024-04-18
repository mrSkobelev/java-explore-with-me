package ru.practicum.request.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.event.model.Event;
import ru.practicum.exception.NotFoundException;
import ru.practicum.request.dto.ConfirmedRequest;
import ru.practicum.request.model.Request;
import ru.practicum.request.model.RequestStatus;

public interface RequestRepository extends JpaRepository<Request, Long> {

    List<Request> findAllByRequester_Id(Long requesterId);

    Optional<Request> findByIdAndRequester_Id(Long requestId, Long requesterId);

    Long countAllByStatusEqualsAndEvent_Id(RequestStatus status, Long eventId);

    List<Request> findAllByEvent_IdAndEvent_Initiator_Id(Long eventId, Long initiatorId);

    @Query("select new ru.practicum.request.dto.ConfirmedRequest(count(r.id), r.event.id) " +
        "from Request as r " +
        "where r.event in :events " +
        "and r.status = :status " +
        "group by r.event.id")
    List<ConfirmedRequest> getConfirmedRequests(RequestStatus status, List<Event> events);

    default Request findRequestByRequestIdAndRequesterId(Long userId, Long requestId) {
        return findByIdAndRequester_Id(requestId, userId).orElseThrow(
            () -> new NotFoundException("Не найден запрос с id: " + requestId + "от пользователя с id: " + userId)
        );
    }

}
