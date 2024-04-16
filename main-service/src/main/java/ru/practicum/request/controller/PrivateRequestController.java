package ru.practicum.request.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.request.dto.ParticipationRequestDto;
import ru.practicum.request.service.RequestService;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class PrivateRequestController {
    private final RequestService service;

    @GetMapping("/{userId}/requests")
    public List<ParticipationRequestDto> getAllRequestsByUserId(@PathVariable Long userId) {
        return service.getAllRequestsByUserId(userId);
    }

    @PostMapping("/{userId}/requests")
    @ResponseStatus(HttpStatus.CREATED)
    public ParticipationRequestDto createRequestByUserId(
        @PathVariable Long userId,
        @RequestParam Long eventId) {
        return service.createRequestByUserId(userId, eventId);
    }

    @PatchMapping("/{userId}/requests/{requestId}/cancel")
    public ParticipationRequestDto cancelRequestByUserId(
        @PathVariable Long userId,
        @PathVariable Long requestId) {
        return service.cancelRequestByUserId(userId, requestId);
    }
}
