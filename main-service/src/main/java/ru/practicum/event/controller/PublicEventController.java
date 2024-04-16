package ru.practicum.event.controller;

import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.client.StatsServiceClient;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.dto.PublicEventQueryParams;
import ru.practicum.event.service.EventService;

@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
public class PublicEventController {
    private final EventService service;
    private final StatsServiceClient statsServiceClient;

    @GetMapping("/{eventId}")
    public EventFullDto getEventById(@PathVariable long eventId, HttpServletRequest request) {
        statsServiceClient.saveStats(request);
        return service.getEventById(eventId);
    }

    @GetMapping
    public List<EventShortDto> getAllEvents(
        @Valid PublicEventQueryParams params,
        HttpServletRequest request) {
        statsServiceClient.saveStats(request);
        return service.getAllEvents(params);
    }
}
