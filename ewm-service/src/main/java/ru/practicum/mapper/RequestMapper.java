package ru.practicum.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.dto.RequestDto;
import ru.practicum.dto.RequestStatusDto;
import ru.practicum.dto.type.RequestStatus;
import ru.practicum.model.Event;
import ru.practicum.model.Request;
import ru.practicum.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.dto.type.RequestStatus.CONFIRMED;
import static ru.practicum.dto.type.RequestStatus.PENDING;

@UtilityClass
public class RequestMapper {

    public List<RequestDto> mapToDtos(List<Request> requests) {
        return requests.stream().map(RequestMapper::mapToDto).collect(Collectors.toList());
    }

    public RequestDto mapToDto(Request request) {
        return RequestDto.builder()
                .id(request.getId())
                .created(request.getCreateDate())
                .event(request.getEvent().getId())
                .requester(request.getRequester().getId())
                .status(request.getStatus())
                .build();
    }

    public RequestStatusDto mapToStatusDto(List<Request> confirmed, List<Request> rejected) {
        return RequestStatusDto.builder()
                .confirmedRequests(mapToDtos(confirmed))
                .rejectedRequests(mapToDtos(rejected))
                .build();
    }

    public Request mapToEntity(User requester, Event event, boolean isPreModerated) {
        RequestStatus status = event.getParticipantLimit() == 0 || !isPreModerated ? CONFIRMED : PENDING;
        return Request.builder()
                .requester(requester)
                .event(event)
                .status(status)
                .createDate(LocalDateTime.now())
                .build();
    }

    public RequestDto toParticipationRequestDto(Request request) {
        return RequestDto.builder()
                .id(request.getId())
                .event(request.getEvent().getId())
                .requester(request.getRequester().getId())
                .status(request.getStatus())
                .created(request.getCreateDate())
                .build();
    }
}