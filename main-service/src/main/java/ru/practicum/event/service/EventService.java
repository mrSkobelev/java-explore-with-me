package ru.practicum.event.service;

import java.util.List;
import ru.practicum.event.dto.AdminEventQueryParams;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.dto.NewEventDto;
import ru.practicum.event.dto.PublicEventQueryParams;
import ru.practicum.event.dto.UpdateEventAdminRequest;
import ru.practicum.event.dto.UpdateEventUserRequest;
import ru.practicum.request.dto.EventRequestStatusUpdateRequest;
import ru.practicum.request.dto.EventRequestStatusUpdateResult;
import ru.practicum.request.dto.ParticipationRequestDto;

public interface EventService {

    EventFullDto getEventById(Long eventId);

    List<EventShortDto> getAllEvents(PublicEventQueryParams params);

    List<EventShortDto> getAllEventsByUserId(Long userId, Integer from, Integer size);

    EventFullDto createEvent(Long userId, NewEventDto newEventDto);

    EventFullDto getEventByUserId(Long userId, Long eventId);

    EventFullDto userUpdateEvent(Long userId, Long eventId, UpdateEventUserRequest request);

    List<ParticipationRequestDto> getAllRequestByUserEvent(Long userId, Long eventId);

    EventRequestStatusUpdateResult confirmRequests(Long userId, Long eventId, EventRequestStatusUpdateRequest request);

    List<EventFullDto> getEventsByFilter(AdminEventQueryParams params);

    EventFullDto adminUpdateEvent(Long eventId, UpdateEventAdminRequest request);
}
