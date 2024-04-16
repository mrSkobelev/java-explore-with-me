package ru.practicum.request.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.experimental.UtilityClass;
import ru.practicum.event.model.Event;
import ru.practicum.request.model.Request;
import ru.practicum.request.model.RequestStatus;
import ru.practicum.user.model.User;

@UtilityClass
public class RequestMapper {
    public Request toRequest(User requester, Event event) {
        Request request = new Request();

        request.setRequester(requester);
        request.setEvent(event);
        request.setCreated(LocalDateTime.now());
        request.setStatus(RequestStatus.PENDING);

        if (!event.getRequestModeration() || event.getParticipantLimit() == 0) {
            request.setStatus(RequestStatus.CONFIRMED);
        }

        return request;
    }

    public ParticipationRequestDto toParticipationRequestDto(Request request) {
        ParticipationRequestDto participationRequestDto = new ParticipationRequestDto();

        participationRequestDto.setId(request.getId());
        participationRequestDto.setRequester(request.getRequester().getId());
        participationRequestDto.setEvent(request.getEvent().getId());
        participationRequestDto.setCreated(request.getCreated());
        participationRequestDto.setStatus(request.getStatus());

        return participationRequestDto;
    }


    public static EventRequestStatusUpdateResult toEventRequestStatusUpdateResults(
        List<Request> confirmedRequestslist,
        List<Request> rejectedRequestslist) {

        EventRequestStatusUpdateResult eventRequestStatusUpdateResult = new EventRequestStatusUpdateResult();

        eventRequestStatusUpdateResult.setConfirmedRequests(confirmedRequestslist
            .stream()
            .map(RequestMapper::toParticipationRequestDto)
            .collect(Collectors.toList()));

        eventRequestStatusUpdateResult.setRejectedRequests(rejectedRequestslist
            .stream()
            .map(RequestMapper::toParticipationRequestDto)
            .collect(Collectors.toList()));

        return eventRequestStatusUpdateResult;
    }
}
