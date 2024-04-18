package ru.practicum.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.dto.RequestDto;
import ru.practicum.dto.event.EventCreateDto;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.event.EventShortDto;
import ru.practicum.dto.type.RequestStatus;
import ru.practicum.model.Event;

import java.util.List;

@UtilityClass
public class EventMapper {
    public Event toEvent(EventCreateDto newEventDto) {
        return Event.builder()
                .title(newEventDto.getTitle())
                .annotation(newEventDto.getAnnotation())
                .description(newEventDto.getDescription())
                .eventDate(newEventDto.getEventDate())
                .location(LocationMapper.mapToEntity(newEventDto.getLocation()))
                .paid(newEventDto.getPaid())
                .participantLimit(newEventDto.getParticipantLimit())
                .requestModeration(newEventDto.getRequestModeration())
                .build();
    }

    public EventFullDto toEventFullDto(Event event) {
        return EventFullDto.builder()
                .id(event.getId())
                .title(event.getTitle())
                .annotation(event.getAnnotation())
                .category(CategoryMapper.mapToDto(event.getCategory()))
                .paid(event.getPaid())
                .eventDate(event.getEventDate())
                .initiator(UserMapper.mapToUserShortDto(event.getInitiator()))
                .views(event.getViews())
                .confirmedRequests(countConfirmedRequests(event.getConfirmedRequests()))
                .description(event.getDescription())
                .participantLimit(event.getParticipantLimit())
                .state(event.getPublicationState())
                .createdOn(event.getCreateDate())
                .publishedOn(event.getPublicationDate() != null ? event.getPublicationDate() : null)
                .location(LocationMapper.mapToDto(event.getLocation()))
                .requestModeration(event.getRequestModeration())
                .build();
    }

    public EventShortDto toEventShortDto(Event event) {
        return EventShortDto.builder()
                .id(event.getId())
                .title(event.getTitle())
                .annotation(event.getAnnotation())
                .category(CategoryMapper.mapToDto(event.getCategory()))
                .eventDate(event.getEventDate())
                .confirmedRequests(countConfirmedRequests(event.getConfirmedRequests()))
                .initiator(UserMapper.mapToUserShortDto(event.getInitiator()))
                .paid(event.getPaid())
                .views(event.getViews())
                .build();
    }

    public long countConfirmedRequests(List<RequestDto> requests) {
        return requests != null
                ? requests.stream().filter(r -> r.getStatus() == RequestStatus.CONFIRMED).count()
                : 0;
    }
}