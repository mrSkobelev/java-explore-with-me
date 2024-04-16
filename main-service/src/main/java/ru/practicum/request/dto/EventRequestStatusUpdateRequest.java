package ru.practicum.request.dto;

import java.util.Set;
import lombok.Data;
import ru.practicum.request.model.RequestStatus;

@Data
public class EventRequestStatusUpdateRequest {
    private Set<Long> requestIds;
    private RequestStatus status;
}
