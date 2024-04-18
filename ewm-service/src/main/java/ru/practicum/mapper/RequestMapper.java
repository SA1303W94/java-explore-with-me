package ru.practicum.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.dto.request.RequestDto;
import ru.practicum.model.Request;

import java.util.List;
import java.util.stream.Collectors;

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