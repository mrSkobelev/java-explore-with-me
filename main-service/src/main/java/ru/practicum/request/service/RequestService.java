package ru.practicum.request.service;

import java.util.List;
import ru.practicum.request.dto.ParticipationRequestDto;

public interface RequestService {

    List<ParticipationRequestDto> getAllRequestsByUserId(Long userId);

    ParticipationRequestDto createRequestByUserId(Long userId, Long eventId);

    ParticipationRequestDto cancelRequestByUserId(Long userId, Long requestId);
}
