package ru.practicum.request.service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.EventState;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.request.dto.ParticipationRequestDto;
import ru.practicum.request.dto.RequestMapper;
import ru.practicum.request.model.Request;
import ru.practicum.request.model.RequestStatus;
import ru.practicum.request.repository.RequestRepository;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {
    private final RequestRepository requestRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    @Override
    public List<ParticipationRequestDto> getAllRequestsByUserId(Long userId) {
        log.info("Получить все запросы на участие пользователя с id: {}", userId);

        List<Request> requests = requestRepository.findAllByRequester_Id(userId);

        if (requests.isEmpty()) {
            log.info("Получен пустой список запросов на участие пользователя с id: {}", userId);

            return Collections.emptyList();
        }

        log.info("Получены все запросы на участие пользователя с id: {}", userId);

        return requests.stream().map(RequestMapper::toParticipationRequestDto).collect(Collectors.toList());
    }

    @Override
    public ParticipationRequestDto createRequestByUserId(Long userId, Long eventId) {
        log.info("Создать запрос на участие пользователем с id: {} в событии с id: {}", userId, eventId);

        User user = validUser(userId);
        Event event = validEvent(eventId);

        if (!event.getState().equals(EventState.PUBLISHED)) {
            throw new ConflictException("Событие ещё не опубликовано");
        }
        if (user.getId() == event.getInitiator().getId()) {
            throw new ConflictException("Автор события не может подавать заявку на участие");
        }

        Long confirmedRequests = requestRepository.countAllByStatusEqualsAndEvent_Id(RequestStatus.CONFIRMED, eventId);

        if (confirmedRequests.equals(event.getParticipantLimit()) && event.getParticipantLimit() != 0) {
            throw new ConflictException("Превышен лимит запросов");
        }

        Request request = RequestMapper.toRequest(user, event);

        try {
            Request savedRequest = requestRepository.save(request);
            log.info("Создан запрос на участие от пользователя с id: {} в событии с id: {}", userId, eventId);

            return RequestMapper.toParticipationRequestDto(savedRequest);

        } catch (DataIntegrityViolationException e) {
            throw new ConflictException("Нельзя повторно добавить запрос");
        }
    }

    @Override
    public ParticipationRequestDto cancelRequestByUserId(Long userId, Long requestId) {
        log.info("Отмена заявки на участие пользователем с id: {}", userId);

        Request request = requestRepository.findRequestByRequestIdAndRequesterId(userId, requestId);

        if (request.getStatus().equals(RequestStatus.PENDING)) {
            request.setStatus(RequestStatus.CANCELED);
        }

        Request canceledRequest = requestRepository.save(request);

        log.info("Успешно отменена заявка на участие пользователя с id: {}", userId);

        return RequestMapper.toParticipationRequestDto(canceledRequest);
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
}
