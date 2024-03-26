package ru.practicum.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.client.StatsClient;
import ru.practicum.constant.FormatConstants;
import ru.practicum.dto.HitDto;
import ru.practicum.dto.StatDto;
import ru.practicum.dto.event.*;
import ru.practicum.dto.type.EventAction;
import ru.practicum.dto.type.EventSort;
import ru.practicum.dto.type.PublicationState;
import ru.practicum.exception.NotFoundException;
import ru.practicum.exception.ValidationConflictException;
import ru.practicum.mapper.EventMapper;
import ru.practicum.mapper.LocationMapper;
import ru.practicum.model.Category;
import ru.practicum.model.Event;
import ru.practicum.model.Location;
import ru.practicum.model.User;
import ru.practicum.repository.CategoryRepository;
import ru.practicum.repository.EventRepository;
import ru.practicum.repository.LocationRepository;
import ru.practicum.repository.UserRepository;
import ru.practicum.service.EventService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ValidationException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static ru.practicum.dto.type.EventAction.PUBLISH_EVENT;
import static ru.practicum.dto.type.EventAction.REJECT_EVENT;
import static ru.practicum.dto.type.PublicationState.PENDING;
import static ru.practicum.dto.type.PublicationState.PUBLISHED;
import static ru.practicum.exception.ExceptionType.*;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final LocationRepository locationRepository;
    private final StatsClient statsClient;

    @Override
    public EventFullDto createEvent(Long userId, EventCreateDto newEventDto) {
        log.info("Create a new event: user_id = {}, event = {}", userId, newEventDto);
        User user = userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException(String.format(USER_NOT_FOUND.getValue(), userId)));
        Category category = categoryRepository.findById(newEventDto.getCategory()).orElseThrow(() ->
                new NotFoundException(String.format(CATEGORY_NOT_FOUND.getValue(), newEventDto.getCategory())));
        Event event = EventMapper.toEvent(newEventDto);
        if (event.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new ValidationConflictException(String.format(EVENT_UNAVAILABLE_FOR_EDITING.getValue()));
        }
        event.setCategory(category);
        event.setCreateDate(LocalDateTime.now());
        event.setInitiator(user);
        event.setPublicationState(PENDING);
        event.setLocation(locationRepository.save(LocationMapper.mapToEntity(newEventDto.getLocation())));
        event.setViews(0L);
        return EventMapper.toEventFullDto(eventRepository.save(event));
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventShortDto> getEvents(Long userId, int from, int size) {
        log.info("Getting events added by the current user: user_id = {} from = {} size = {}", userId, from, size);
        userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException(String.format(USER_NOT_FOUND.getValue(), userId)));
        List<Event> events = eventRepository.findByInitiatorId(userId, PageRequest.of(from / size, size));
        return events.stream()
                .map(EventMapper::toEventShortDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public EventFullDto getEventById(Long userId, Long eventId) {
        log.info("Getting full information about the event added by the current user: user_id = {}, event_id = {}",
                userId, eventId);
        userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException(String.format(USER_NOT_FOUND.getValue(), userId)));
        return EventMapper.toEventFullDto(eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(String.format(EVENT_NOT_FOUND.getValue(), eventId))));
    }

    @Override
    public EventFullDto updateEventByUser(Long userId, Long eventId, EventUpdateUserDto updateEventUserRequestDto) {
            log.info("Updating event information: user_id = {}, event_id = {}, updateEventUserRequestDto = {}",
                    userId, eventId, updateEventUserRequestDto);
        userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException(String.format(USER_NOT_FOUND.getValue(), userId)));
        Event event = eventRepository.findById(eventId).orElseThrow(() ->
                new NotFoundException(String.format(EVENT_NOT_FOUND.getValue(), eventId)));
        if (event.getPublicationState() != null && event.getPublicationState() != PENDING
                && event.getPublicationState() != PublicationState.CANCELED) {
            throw new ValidationConflictException(String.format(EVENT_UNAVAILABLE_FOR_EDITING.getValue()));
        }
        if (updateEventUserRequestDto.getEventDate() != null
                && updateEventUserRequestDto.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new ValidationConflictException(String.format(EVENT_UNAVAILABLE_FOR_EDITING.getValue(),
                    updateEventUserRequestDto.getEventDate()));
        }
        if (updateEventUserRequestDto.getTitle() != null) {
            event.setTitle(updateEventUserRequestDto.getTitle());
        }
        if (updateEventUserRequestDto.getAnnotation() != null) {
            event.setAnnotation(updateEventUserRequestDto.getAnnotation());
        }
        if (updateEventUserRequestDto.getCategory() != null) {
            event.setCategory(categoryRepository.findById(updateEventUserRequestDto.getCategory())
                    .orElseThrow(() -> new NotFoundException(String.format(CATEGORY_NOT_FOUND.getValue(),
                            updateEventUserRequestDto.getCategory()))));
        }
        if (updateEventUserRequestDto.getDescription() != null) {
            event.setDescription(updateEventUserRequestDto.getDescription());
        }
        if (updateEventUserRequestDto.getEventDate() != null) {
            event.setEventDate(updateEventUserRequestDto.getEventDate());
        }
        if (updateEventUserRequestDto.getLocation() != null) {
            Location location = event.getLocation();
            location.setLatitude(updateEventUserRequestDto.getLocation().getLat());
            location.setLongitude(updateEventUserRequestDto.getLocation().getLon());
            event.setLocation(location);
            locationRepository.save(location);
        }
        if (updateEventUserRequestDto.getPaid() != null) {
            event.setPaid(updateEventUserRequestDto.getPaid());
        }
        if (updateEventUserRequestDto.getParticipantLimit() != null) {
            event.setParticipantLimit(updateEventUserRequestDto.getParticipantLimit());
        }
        if (updateEventUserRequestDto.getRequestModeration() != null) {
            event.setRequestModeration(updateEventUserRequestDto.getRequestModeration());
        }
        if (updateEventUserRequestDto.getParticipantLimit() != null) {
            event.setParticipantLimit(updateEventUserRequestDto.getParticipantLimit());
        }
        if (updateEventUserRequestDto.getStateAction() == null) {
            return EventMapper.toEventFullDto(eventRepository.save(event));
        }
        if (updateEventUserRequestDto.getStateAction() == EventAction.CANCEL_REVIEW) {
            event.setPublicationState(PublicationState.CANCELED);
        }
        if (updateEventUserRequestDto.getStateAction() == EventAction.SEND_TO_REVIEW) {
            event.setPublicationState(PENDING);
        }
        return EventMapper.toEventFullDto(eventRepository.save(event));
    }

    @Override
    public EventFullDto updateEventByAdmin(Long eventId, EventUpdateAdminDto updateEventAdminRequestDto) {
        log.info("updating information about the event by the administrator: event_id = {}, " +
                        "updateEventAdminRequestDto = {}", eventId, updateEventAdminRequestDto);
        Event event = eventRepository.findById(eventId).orElseThrow(() ->
                new NotFoundException(String.format(EVENT_NOT_FOUND.getValue(), eventId)));
        if (updateEventAdminRequestDto.getStateAction() != null) {
            if (updateEventAdminRequestDto.getStateAction() == PUBLISH_EVENT) {
                if (event.getPublicationState() != PENDING) {
                    throw new ValidationConflictException(String.format(EVENT_MUST_BE_PENDING.getValue()));
                }
                if (event.getPublicationDate() != null && event.getEventDate().isAfter(event.getPublicationDate().minusHours(1))) {
                    throw new ValidationConflictException(String.format(EVENT_UNAVAILABLE_FOR_EDITING_ADMIN.getValue()));
                }
                event.setPublicationDate(LocalDateTime.now());
                event.setPublicationState(PUBLISHED);
            }
            if (updateEventAdminRequestDto.getStateAction() == REJECT_EVENT) {
                if (event.getPublicationState() == PUBLISHED) {
                    throw new ValidationConflictException(String.format(EVENT_ALREADY_PUBLISHED.getValue(), eventId));
                } else {
                    event.setPublicationState(PublicationState.CANCELED);
                }
            }
        }
        if (updateEventAdminRequestDto.getEventDate() != null
                && updateEventAdminRequestDto.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new ValidationConflictException(String.format(EVENT_UNAVAILABLE_FOR_EDITING.getValue(),
                    updateEventAdminRequestDto.getEventDate()));
        }
        if (updateEventAdminRequestDto.getTitle() != null) {
            event.setTitle(updateEventAdminRequestDto.getTitle());
        }
        if (updateEventAdminRequestDto.getAnnotation() != null) {
            event.setAnnotation(updateEventAdminRequestDto.getAnnotation());
        }
        if (updateEventAdminRequestDto.getCategory() != null) {
            event.setCategory(categoryRepository.findById(updateEventAdminRequestDto.getCategory())
                    .orElseThrow(() -> new NotFoundException(String.format(CATEGORY_NOT_FOUND.getValue(), updateEventAdminRequestDto.getCategory()))));
        }
        if (updateEventAdminRequestDto.getDescription() != null) {
            event.setDescription(updateEventAdminRequestDto.getDescription());
        }
        if (updateEventAdminRequestDto.getEventDate() != null) {
            event.setEventDate(updateEventAdminRequestDto.getEventDate());
        }
        if (updateEventAdminRequestDto.getLocation() != null) {
            Location location = event.getLocation();
            location.setLatitude(updateEventAdminRequestDto.getLocation().getLat());
            location.setLongitude(updateEventAdminRequestDto.getLocation().getLon());
            event.setLocation(location);
            locationRepository.save(location);
        }
        if (updateEventAdminRequestDto.getPaid() != null) {
            event.setPaid(updateEventAdminRequestDto.getPaid());
        }
        if (updateEventAdminRequestDto.getParticipantLimit() != null) {
            event.setParticipantLimit(updateEventAdminRequestDto.getParticipantLimit());
        }
        if (updateEventAdminRequestDto.getRequestModeration() != null) {
            event.setRequestModeration(updateEventAdminRequestDto.getRequestModeration());
        }
        if (updateEventAdminRequestDto.getParticipantLimit() != null) {
            event.setParticipantLimit(updateEventAdminRequestDto.getParticipantLimit());
        }
        return EventMapper.toEventFullDto(eventRepository.save(event));
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventFullDto> getEventsByAdmin(List<Long> users, List<String> states, List<Long> categories,
                                               String rangeStart, String rangeEnd, int from, int size) {
        log.info("Search for events by parameters: user_ids = {}, states = {}, categories = {}, rangeStart = {}, " +
                "rangeEnd = {}, from = {}, size = {}", users, states, categories, rangeStart, rangeEnd, from, size);
        validateEventStates(states);
        List<Event> events = eventRepository.findEvents(users,
                states, categories,
                rangeStart != null ? LocalDateTime.parse(rangeStart, FormatConstants.FORMATTER) : null,
                rangeEnd != null ? LocalDateTime.parse(rangeEnd, FormatConstants.FORMATTER) : null,
                PageRequest.of(from / size, size));
        return events
                .stream()
                .map(EventMapper::toEventFullDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<EventShortDto> getPublishedEvents(String text, List<Long> categories,
                                                  Boolean paid, String rangeStart,
                                                  String rangeEnd, boolean onlyAvailable,
                                                  String sort, int from, int size,
                                                  HttpServletRequest request) {
        log.info("Search for published events by parameters: text = {}, categories = {}, paid = {}, rangeStart = {}, " +
                "rangeEnd = {}, onlyAvailable = {}, sort = {}, from = {}, size = {}",
                text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size);
        statsClient.create(HitDto.builder()
                .app("ewm-service")
                .uri(request.getRequestURI())
                .ip(request.getRemoteAddr())
                .timestamp(LocalDateTime.now())
                .build());
        if (rangeStart != null && rangeEnd != null &&
                LocalDateTime.parse(rangeStart, FormatConstants.FORMATTER)
                        .isAfter(LocalDateTime.parse(rangeEnd, FormatConstants.FORMATTER))) {
            throw new ValidationException(String.format(START_DATE_IN_PAST.getValue(), rangeStart));
        }
        List<Event> events = eventRepository.findPublishedEvents(
                text,
                categories,
                paid,
                rangeStart != null ? LocalDateTime.parse(rangeStart, FormatConstants.FORMATTER) : LocalDateTime.now(),
                rangeEnd != null ? LocalDateTime.parse(rangeEnd, FormatConstants.FORMATTER) : null,
                PageRequest.of(from / size, size));
        List<EventShortDto> eventShortDtos = Collections.emptyList();
        if (events != null) {
            eventShortDtos = events.stream().map(EventMapper::toEventShortDto).collect(Collectors.toList());
            if (onlyAvailable) {
                eventShortDtos = events.stream().filter(event ->
                        EventMapper.countConfirmedRequests(event.getRequests()) < event.getParticipantLimit()
                ).map(EventMapper::toEventShortDto).collect(Collectors.toList());
            }
            if (sort != null) {
                switch (EventSort.valueOf(sort)) {
                    case EVENT_DATE:
                        eventShortDtos.sort(Comparator.comparing(EventShortDto::getEventDate));
                        break;
                    case VIEWS:
                        eventShortDtos.sort(Comparator.comparing(EventShortDto::getViews));
                        break;
                    default:
                        throw new NotFoundException(String.format(STATE_NOT_FOUND.getValue(), sort));
                }
            }
        }
        return eventShortDtos;
    }

    @Override
    public EventFullDto getPublishedEventById(Long eventId, HttpServletRequest request) {
        log.info("Getting information about a published event by ID: event_id = {}", eventId);
        Event event = eventRepository.findByIdAndPublicationState(eventId, PUBLISHED)
                .orElseThrow(() -> new NotFoundException(String.format(EVENT_NOT_FOUND.getValue(), eventId)));
        Long countHits = getCountHits(request);
        statsClient.create(HitDto.builder()
                .app("ewm-service")
                .uri(request.getRequestURI())
                .ip(request.getRemoteAddr())
                .timestamp(LocalDateTime.now())
                .build());

        Long newCountHits = getCountHits(request);

        if (newCountHits != null && newCountHits > countHits) {
            event.setViews(event.getViews() + 1);
            eventRepository.save(event);
        }
        return EventMapper.toEventFullDto(event);
    }

    private void validateEventStates(List<String> states) {
        if (states != null) {
            for (String state : states)
                try {
                    PublicationState.valueOf(state);
                } catch (IllegalArgumentException e) {
                    throw new NotFoundException(String.format(STATE_NOT_FOUND.getValue(), state));
                }
        }
    }

    private Long getCountHits(HttpServletRequest request) {
        log.info("Client ip: {}", request.getRemoteAddr());
        log.info("Endpoint path: {}", request.getRequestURI());
        ResponseEntity<StatDto[]> response = statsClient.getStats(
                LocalDateTime.now().minusYears(100).format(FormatConstants.FORMATTER),
                LocalDateTime.now().format(FormatConstants.FORMATTER),
                new String[] {request.getRequestURI()},
                true);
        Optional<StatDto> statDto;
        Long hits = 0L;
        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            statDto = Arrays.stream(response.getBody()).findFirst();
            if (statDto.isPresent()) {
                hits = statDto.get().getHits();
            }
        }
        return hits;
    }
}