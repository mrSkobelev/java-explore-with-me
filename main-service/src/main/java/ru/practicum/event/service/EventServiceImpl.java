package ru.practicum.event.service;

import static java.util.stream.Collectors.toMap;

import com.querydsl.core.types.dsl.BooleanExpression;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.category.model.Category;
import ru.practicum.category.repository.CategoryRepository;
import ru.practicum.client.StatsServiceClient;
import ru.practicum.dto.ViewStatsDto;
import ru.practicum.event.dto.AdminEventQueryParams;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventMapper;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.dto.NewEventDto;
import ru.practicum.event.dto.PublicEventQueryParams;
import ru.practicum.event.dto.UpdateEventAdminRequest;
import ru.practicum.event.dto.UpdateEventRequest;
import ru.practicum.event.dto.UpdateEventUserRequest;
import ru.practicum.event.dto.UpdateEventUserRequest.StateAction;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.EventSort;
import ru.practicum.event.model.EventState;
import ru.practicum.event.model.QEvent;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.exception.ValidationException;
import ru.practicum.location.model.Location;
import ru.practicum.location.repository.LocationRepository;
import ru.practicum.request.dto.ConfirmedRequest;
import ru.practicum.request.dto.EventRequestStatusUpdateRequest;
import ru.practicum.request.dto.EventRequestStatusUpdateResult;
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
public class EventServiceImpl implements EventService {
    private static final String EVENT_URI = "/events/";

    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final RequestRepository requestRepository;
    private final LocationRepository locationRepository;
    private final StatsServiceClient statsServiceClient;

    @Override
    public EventFullDto getEventById(Long eventId) {
        Event event = eventRepository.findByIdAndStateEquals(eventId, EventState.PUBLISHED)
            .orElseThrow(() -> new NotFoundException("Не найдено событие с id: " + eventId));
        long confirmedRequests = getCountConfirmedRequests(eventId);
        long views = getViewsByEvent(event);
        log.debug("Выгружена полная информация по событию с eventId=" + eventId);

        return EventMapper.toEventFullDto(event, confirmedRequests, views);
    }

    @Override
    public List<EventShortDto> getAllEvents(PublicEventQueryParams params) {
        QEvent qEvent = QEvent.event;

        List<BooleanExpression> conditions = new ArrayList<>();

        conditions.add(qEvent.state.eq(EventState.PUBLISHED));

        if (params.getText() != null) {
            conditions.add(qEvent.description.equalsIgnoreCase(params.getText())
                .or(qEvent.annotation.equalsIgnoreCase(params.getText())));
        }

        if (params.getCategories() != null) {
            conditions.add(qEvent.category.id.in(params.getCategories()));
        }

        if (params.getPaid() != null) {
            conditions.add(qEvent.paid.eq(params.getPaid()));
        }

        if (params.getRangeStart() != null && params.getRangeEnd() != null) {
            validSearchEventDate(params.getRangeStart(), params.getRangeEnd());
            conditions.add(qEvent.eventDate.between(params.getRangeStart(), params.getRangeEnd()));
        } else {
            conditions.add(qEvent.eventDate.after(LocalDateTime.now()));
        }

        Optional<BooleanExpression> generalCondition = conditions.stream().reduce(BooleanExpression::and);

        Sort sort = null;
        EventSort eventSort = params.getSort();

        if (eventSort != null) {
            if (eventSort.equals(EventSort.EVENT_DATE)) {
                sort = Sort.by(Sort.Direction.ASC, "eventDate");
            }
            if (eventSort.equals(EventSort.VIEWS)) {
                sort = Sort.by(Sort.Direction.ASC, "views");
            }
        }

        int from = params.getFrom();
        int size = params.getSize();

        PageRequest pageRequest;
        if (sort != null) {
            pageRequest = PageRequest.of(from / size, size, sort);
        } else {
            pageRequest = PageRequest.of(from / size, size);
        }

        List<Event> events = generalCondition
            .map(booleanExpression -> eventRepository.findAll(booleanExpression, pageRequest)
                .getContent())
            .orElseGet(() -> eventRepository.findAll(pageRequest).getContent());

        if (events.isEmpty()) {
            return Collections.emptyList();
        }

        log.info("Выгружен список событий: {}", events);

        Map<Long, Long> confirmedRequests = getConfirmedRequestsByEvent(events);
        Map<String, Long> views = getViewsByUriMaps(events);

        return events.stream().map(event -> EventMapper.toEventShortDto(
                event,
                confirmedRequests.getOrDefault(event.getId(), 0L),
                views.getOrDefault(EVENT_URI + event.getId(), 0L)))
            .collect(Collectors.toList());
    }

    @Override
    public List<EventShortDto> getAllEventsByUserId(Long userId, Integer from, Integer size) {
        log.info("Получить список событий пользователя с id: {}", userId);

        validUser(userId);
        validPagination(from, size);

        PageRequest pageRequest = PageRequest.of(from / size, size);
        List<Event> events = eventRepository.findByInitiatorId(userId, pageRequest).getContent();

        if (events.isEmpty()) {
            return Collections.emptyList();
        }

        Map<Long, Long> countConfirmedRequestsByEventId = getConfirmedRequestsByEvent(events);
        Map<String, Long> viewsByUriMaps = getViewsByUriMaps(events);

        log.info("Получен список событий от пользователя с id: {}", userId);

        return events.stream()
            .map(event -> EventMapper.toEventShortDto(
                event,
                countConfirmedRequestsByEventId.getOrDefault(event.getId(), 0L),
                viewsByUriMaps.getOrDefault(EVENT_URI + event.getId(), 0L)))
            .collect(Collectors.toList());
    }

    @Override
    public EventFullDto createEvent(Long userId, NewEventDto newEventDto) {
        log.info("Создать событие от пользователя с id: {}", userId);

        User initiator = validUser(userId);
        Category category = validCategory(newEventDto.getCategory());
        validCreateEventDate(newEventDto.getEventDate());
        Location location = locationRepository.save(newEventDto.getLocation());

        Event event = EventMapper.toEvent(newEventDto, initiator, category, location);

        if (event.getPaid() == null) {
            event.setPaid(false);
        }

        if (event.getParticipantLimit() == null) {
            event.setParticipantLimit(0L);
        }

        if (event.getRequestModeration() == null) {
            event.setRequestModeration(true);
        }

        Event savedEvent = eventRepository.save(event);

        long confirmedRequests = requestRepository.countAllByStatusEqualsAndEvent_Id(RequestStatus.CONFIRMED, savedEvent.getId());
        long view = 0;

        log.info("Создано событие от пользователя с id: {}", userId);

        return EventMapper.toEventFullDto(savedEvent, confirmedRequests, view);
    }

    @Override
    public EventFullDto getEventByUserId(Long userId, Long eventId) {
        log.info("Получить событие с id: {} от пользователя с id: {}", eventId, userId);

        Event event = getEventByUserIdAndEventId(userId, eventId);

        long confirmedRequests = getCountConfirmedRequests(eventId);
        long views = getViewsByEvent(event);

        log.info("Получено событие с id: {} от пользователя с id: {}", eventId, userId);

        return EventMapper.toEventFullDto(event, confirmedRequests, views);
    }

    @Override
    public EventFullDto userUpdateEvent(Long userId, Long eventId, UpdateEventUserRequest request) {
        log.info("Обновить пользователем событие с id: {}", eventId);

        validUser(userId);
        Event event = getEventByUserIdAndEventId(userId, eventId);
        EventState state = event.getState();
        Event newEvent;
        Event updatedEvent;

        if (state.equals(EventState.CANCELED) || state.equals(EventState.PENDING)) {

            newEvent = updateFieldsEvent(event, request);

            if (request.getStateAction() != null &&
                request.getStateAction().equals(StateAction.CANCEL_REVIEW)) {

                newEvent.setState(EventState.CANCELED);
                updatedEvent = eventRepository.save(newEvent);

            } else if (request.getStateAction() != null &&
                    request.getStateAction().equals(StateAction.SEND_TO_REVIEW)) {

                newEvent.setState(EventState.PENDING);
                updatedEvent = eventRepository.save(newEvent);

            } else if (request.getStateAction() == null) {
                updatedEvent = eventRepository.save(newEvent);
            } else {
                throw new ConflictException("Невалидный статус события");
            }

        } else {
            throw new ConflictException("Нельзя редактировать опубликованное событие");
        }

        long confirmedRequests = getCountConfirmedRequests(eventId);
        long views = getViewsByEvent(updatedEvent);

        log.info("Обновлено пользователем событие с id: {}", eventId);

        return EventMapper.toEventFullDto(event, confirmedRequests, views);
    }

    @Override
    public List<ParticipationRequestDto> getAllRequestByUserEvent(Long userId, Long eventId) {
        log.info("Получить список запросов на событие с id: {} от пользователя с id: {}", eventId, userId);

        List<Request> requests = requestRepository.findAllByEvent_IdAndEvent_Initiator_Id(eventId, userId);

        if (requests.isEmpty()) {
            return Collections.emptyList();
        }

        log.info("Получен список запросов на событие с id: {} от пользователя с id: {}", eventId, userId);

        return requests.stream().map(RequestMapper::toParticipationRequestDto).collect(Collectors.toList());
    }

    @Override
    public EventRequestStatusUpdateResult confirmRequests(Long userId, Long eventId, EventRequestStatusUpdateRequest request) {
        log.info("Изменить статус заявки от пользователя с id: {} в событии с id: {}", userId, eventId);

        validEvent(eventId);
        validUser(userId);

        Event event = getEventByUserIdAndEventId(userId, eventId);

        RequestStatus status = request.getStatus();
        Set<Long> requestsIds = request.getRequestIds();
        List<Long> requestsIdsList = new ArrayList<>(requestsIds);

        List<Request> requests = requestRepository.findAllById(requestsIdsList);

        List<Request> confirmedlist = new ArrayList<>();
        List<Request> rejectedlist = new ArrayList<>();

        if (requests.size() == 1 &&
            requests.get(0) != null &&
            requests.get(0).getStatus().equals(RequestStatus.CONFIRMED)) {
            throw new ConflictException("Заявка уже одобрена");
        }

        if (status != null && status.equals(RequestStatus.REJECTED)) {
            for (Request r : requests) {
                r.setStatus(RequestStatus.REJECTED);
                rejectedlist.add(r);
            }

            requestRepository.saveAll(rejectedlist);

            log.info("Событие c id: {} -  отказано: {}", eventId, rejectedlist);

            return RequestMapper.toEventRequestStatusUpdateResults(confirmedlist, rejectedlist);
        }

        if (!event.getRequestModeration() || event.getParticipantLimit() == 0) {
            for (Request r : requests) {
                r.setStatus(RequestStatus.CONFIRMED);
                confirmedlist.add(r);
            }
            requestRepository.saveAll(confirmedlist);
            return RequestMapper.toEventRequestStatusUpdateResults(confirmedlist, rejectedlist);
        }

        long patchConfirmedRequests = getCountConfirmedRequests(eventId);
        if (requests.size() == 1 && patchConfirmedRequests == event.getParticipantLimit()) {
                throw new ConflictException("Достигнут максимум участников");
        }

        long i = 0;
        for (Request r : requests) {
            if ((patchConfirmedRequests + i) < event.getParticipantLimit()) {
                r.setStatus(RequestStatus.CONFIRMED);
                confirmedlist.add(r);
                i++;
            } else {
                r.setStatus(RequestStatus.REJECTED);
                rejectedlist.add(r);
            }
        }

        requestRepository.saveAll(confirmedlist);
        requestRepository.saveAll(rejectedlist);

        log.info("Событие c id: {}, отказано : {}, одобрено: {}",
            eventId, rejectedlist, confirmedlist);

        return RequestMapper.toEventRequestStatusUpdateResults(confirmedlist, rejectedlist);
    }

    @Override
    public List<EventFullDto> getEventsByFilter(AdminEventQueryParams params) {
        log.info("Получить админом список событий по параметрам");

        QEvent qEvent = QEvent.event;

        List<BooleanExpression> conditions = new ArrayList<>();

        if (params.getUsers() != null) {
            conditions.add(qEvent.initiator.id.in(params.getUsers()));
        }

        if (params.getStates() != null) {
            conditions.add(qEvent.state.in(params.getStates()));
        }

        if (params.getCategories() != null) {
            conditions.add(qEvent.category.id.in(params.getCategories()));
        }

        if (params.getRangeStart() != null && params.getRangeEnd() != null) {
            validSearchEventDate(params.getRangeStart(), params.getRangeEnd());
            conditions.add(qEvent.eventDate.between(params.getRangeStart(), params.getRangeEnd()));
        }

        Optional<BooleanExpression> generalCondition = conditions.stream().reduce(BooleanExpression::and);

        int from = params.getFrom();
        int size = params.getSize();

        validPagination(from, size);

        PageRequest pageRequest = PageRequest.of(from / size, size);

        List<Event> events = generalCondition
            .map(booleanExpression -> eventRepository
                .findAll(booleanExpression, pageRequest)
                .getContent())
            .orElseGet(() -> eventRepository.findAll(pageRequest).getContent());

        if (events.isEmpty()) {
            log.info("Получен админом пустой список событий по параметрам");

            return Collections.emptyList();
        }

        Map<Long, Long> confirmedRequestsByEventMap = getConfirmedRequestsByEvent(events);
        Map<String, Long> views = getViewsByUriMaps(events);

        log.info("Получен админом список событий");

        return events.stream()
            .map(event -> EventMapper.toEventFullDto(
                event,
                confirmedRequestsByEventMap.getOrDefault(event.getId(), 0L),
                views.getOrDefault(EVENT_URI + event.getId(), 0L)))
            .collect(Collectors.toList());
    }

    @Override
    public EventFullDto adminUpdateEvent(Long eventId, UpdateEventAdminRequest request) {
        log.info("Обновить админом событие с id: {}", eventId);
        Event event = validEvent(eventId);

        Event newEvent = updateFieldsEvent(event, request);
        Event updatedEvent;

        if (request.getStateAction() != null &&
            request.getStateAction().equals(UpdateEventAdminRequest.StateAction.PUBLISH_EVENT) &&
            event.getState().equals(EventState.PENDING)) {

            newEvent.setState(EventState.PUBLISHED);
            newEvent.setPublishedOn(LocalDateTime.now());
            updatedEvent = eventRepository.save(newEvent);

        } else if (request.getStateAction() != null &&
            request.getStateAction().equals(UpdateEventAdminRequest.StateAction.REJECT_EVENT)
            && !event.getState().equals(EventState.PUBLISHED)) {

            newEvent.setState(EventState.CANCELED);
            updatedEvent = eventRepository.save(newEvent);

        } else if (request.getStateAction() == null) {
            updatedEvent = eventRepository.save(newEvent);
        } else {
            throw new ConflictException("Невалидный stateAction");
        }

        long confirmedRequests = getCountConfirmedRequests(eventId);
        long views = getViewsByEvent(updatedEvent);

        log.info("Обновлено пользователем событие с id: {}", eventId);

        return EventMapper.toEventFullDto(updatedEvent, confirmedRequests, views);
    }

    private User validUser(long userId) {
        return userRepository.findById(userId).orElseThrow(
            () -> new NotFoundException("Не найден пользователь с id: " + userId)
        );
    }

    private Event validEvent(Long eventId) {
        return eventRepository.findById(eventId).orElseThrow(
            () -> new NotFoundException("Не найдено событие с id: " + eventId)
        );
    }

    private Category validCategory(long categoryId) {
        return categoryRepository.findById(categoryId).orElseThrow(
            () -> new NotFoundException("Не найдена категория с id: " + categoryId)
        );
    }

    private void validPagination(Integer from, Integer size) {
        if (from < 0 || size < 0) {
            throw new ValidationException("Параметры пагинации не должны быть отрицательными");
        }
    }

    private void validCreateEventDate(LocalDateTime eventDate) {
        if (eventDate.isBefore(LocalDateTime.now().plusHours(2))) {
            throw new ValidationException("Дата события должна быть не раньше, чем через 2 часа");
        }
    }

    private void validSearchEventDate(LocalDateTime start, LocalDateTime end) {
        if (start.isAfter(end)) {
            throw new ValidationException("Дата начала события не может быть позже даты конца события");
        }
    }

    private boolean equalsLocations(Location first, Location second) {
        return (first.getLat().equals(second.getLat()) &&
            first.getLon().equals(second.getLon()));
    }

    private long getCountConfirmedRequests(Long eventId) {
        return requestRepository.countAllByStatusEqualsAndEvent_Id(RequestStatus.CONFIRMED, eventId);
    }

    private Map<Long, Long> getConfirmedRequestsByEvent(List<Event> events) {
        return requestRepository.getConfirmedRequests(RequestStatus.CONFIRMED, events)
            .stream()
            .collect(toMap(ConfirmedRequest::getEventId, ConfirmedRequest::getCount));
    }

    private long getViewsByEvent(Event event) {
        List<ViewStatsDto> viewStatsDtos = statsServiceClient.getViews(List.of(event));

        if (viewStatsDtos == null || viewStatsDtos.isEmpty()) {
            return 0;
        }
        return viewStatsDtos.get(0).getHits();
    }

    private Map<String, Long> getViewsByUriMaps(List<Event> events) {
        return statsServiceClient.getViews(events)
            .stream()
            .collect(toMap(ViewStatsDto::getUri, ViewStatsDto::getHits));
    }

    private Event getEventByUserIdAndEventId(Long userId, Long eventId) {
        return eventRepository.findByIdAndInitiator_Id(eventId, userId).orElseThrow(
            () -> new NotFoundException("Не найдено событие с id: " + eventId + " от пользователя с id: " + userId)
        );
    }

    private Event updateFieldsEvent(Event event, UpdateEventRequest request) {
        if (request.getAnnotation() != null && !request.getAnnotation().isBlank()) {
            event.setAnnotation(request.getAnnotation());
        }

        if (request.getCategory() != null) {
            event.setCategory(validCategory(request.getCategory()));
        }

        if (request.getDescription() != null && !request.getDescription().isBlank()) {
            event.setDescription(request.getDescription());
        }

        if (request.getEventDate() != null) {
            validCreateEventDate(request.getEventDate());
            event.setEventDate(request.getEventDate());
        }

        if (request.getLocation() != null &&
            !equalsLocations(request.getLocation(), event.getLocation())) {
            event.setLocation(request.getLocation());
            locationRepository.save(request.getLocation());
        }

        if (request.getPaid() != null) {
            event.setPaid(request.getPaid());
        }

        if (request.getParticipantLimit() != null) {
            event.setParticipantLimit(request.getParticipantLimit());
        }

        if (request.getRequestModeration() != null) {
            event.setRequestModeration(request.getRequestModeration());
        }

        if (request.getTitle() != null) {
            event.setTitle(request.getTitle());
        }

        return event;
    }
}
