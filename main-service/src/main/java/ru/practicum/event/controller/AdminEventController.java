package ru.practicum.event.controller;

import java.util.List;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.event.dto.AdminEventQueryParams;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.UpdateEventAdminRequest;
import ru.practicum.event.service.EventService;

@RestController
@RequestMapping("/admin/events")
@RequiredArgsConstructor
public class AdminEventController {
    private final EventService service;

    @GetMapping
    public List<EventFullDto> getEventsByFilter(@Valid AdminEventQueryParams params) {
        return service.getEventsByFilter(params);
    }

    @PatchMapping("/{eventId}")
    public EventFullDto adminUpdateEvent(@PathVariable Long eventId,
        @Valid @RequestBody UpdateEventAdminRequest request) {
        return service.adminUpdateEvent(eventId, request);
    }
}
