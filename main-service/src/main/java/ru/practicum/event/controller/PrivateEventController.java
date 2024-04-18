package ru.practicum.event.controller;

import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.dto.NewEventDto;
import ru.practicum.event.dto.UpdateEventUserRequest;
import ru.practicum.event.service.EventService;
import ru.practicum.request.dto.EventRequestStatusUpdateRequest;
import ru.practicum.request.dto.EventRequestStatusUpdateResult;
import ru.practicum.request.dto.ParticipationRequestDto;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class PrivateEventController {
    private final EventService service;

    @GetMapping("/{userId}/events")
    public List<EventShortDto> getAllEventsByUserId(
        @PathVariable Long userId,
        @RequestParam(name = "from", defaultValue = "0") @Min(0) Integer from,
        @RequestParam(name = "size", defaultValue = "10") @Min(1) Integer size) {
        return service.getAllEventsByUserId(userId, from, size);
    }

    @PostMapping("/{userId}/events")
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto createEvent(@PathVariable Long userId, @Valid @RequestBody NewEventDto newEventDto) {
        return service.createEvent(userId, newEventDto);
    }

    @GetMapping("/{userId}/events/{eventId}")
    public EventFullDto getEventByUserId(@PathVariable Long userId, @PathVariable Long eventId) {
        return service.getEventByUserId(userId, eventId);
    }

    @PatchMapping("/{userId}/events/{eventId}")
    public EventFullDto userUpdateEvent(
        @PathVariable Long userId,
        @PathVariable Long eventId,
        @Valid @RequestBody UpdateEventUserRequest request) {
        return service.userUpdateEvent(userId, eventId, request);
    }

    @GetMapping("/{userId}/events/{eventId}/requests")
    public List<ParticipationRequestDto> getAllRequestByUserEvent(
        @PathVariable Long userId,
        @PathVariable Long eventId) {
        return service.getAllRequestByUserEvent(userId, eventId);
    }

    @PatchMapping("/{userId}/events/{eventId}/requests")
    public EventRequestStatusUpdateResult confirmRequests(
        @PathVariable Long userId,
        @PathVariable Long eventId,
        @Valid @RequestBody EventRequestStatusUpdateRequest request) {
        return service.confirmRequests(userId, eventId, request);
    }
}
