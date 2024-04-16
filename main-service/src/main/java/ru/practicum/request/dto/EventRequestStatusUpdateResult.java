package ru.practicum.request.dto;

import java.util.List;
import lombok.Data;

@Data
public class EventRequestStatusUpdateResult {
    private List<ParticipationRequestDto> confirmedRequests;
    private List<ParticipationRequestDto> rejectedRequests;
}
