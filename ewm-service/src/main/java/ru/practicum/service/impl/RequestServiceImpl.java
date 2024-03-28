package ru.practicum.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.dto.RequestDto;
import ru.practicum.dto.RequestStatusDto;
import ru.practicum.dto.RequestStatusUpdateDto;
import ru.practicum.dto.type.PublicationState;
import ru.practicum.dto.type.RequestStatus;
import ru.practicum.exception.NotFoundException;
import ru.practicum.exception.ValidationConflictException;
import ru.practicum.mapper.RequestMapper;
import ru.practicum.model.Event;
import ru.practicum.model.Request;
import ru.practicum.model.User;
import ru.practicum.repository.EventRepository;
import ru.practicum.repository.RequestRepository;
import ru.practicum.repository.UserRepository;
import ru.practicum.service.RequestService;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static ru.practicum.exception.ExceptionType.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class RequestServiceImpl implements RequestService {
    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    @Override
    public RequestDto create(Long userId, Long eventId) {
        log.info("Adding a request from the current user to participate in the event: " +
                "user_id = {}, event_id = {}", userId, eventId);
        User user = userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException(String.format(USER_NOT_FOUND.getValue(), userId)));
        Event event = eventRepository.findById(eventId).orElseThrow(() ->
                new NotFoundException(String.format(EVENT_NOT_FOUND.getValue(), eventId)));
        Request existParticipationRequest = requestRepository.findByRequesterIdAndEventId(userId, eventId);
        if (existParticipationRequest != null) {
            throw new ValidationConflictException(String.format(REQUEST_DUPLICATE.getValue(), userId, eventId));
        }
        if (event.getInitiator().getId().equals(userId)) {
            throw new ValidationConflictException(String.format(EVENT_INITIATED_BY_REQUESTER.getValue()));
        }
        if (event.getPublicationState() != PublicationState.PUBLISHED) {
            throw new ValidationConflictException(String.format(REQUEST_FOR_UNPUBLISHED_EVENT.getValue()));
        }
        if (event.getParticipantLimit() != 0 && requestRepository.countByEventIdAndStatus(eventId, RequestStatus.CONFIRMED) >= event.getParticipantLimit()) {
            throw new ValidationConflictException(String.format(REACHED_PARTICIPANT_LIMIT.getValue()));
        }
//        if (event.getRequestModeration()) {
//            event.setConfirmedRequests(event.getConfirmedRequests() + 1);
//            eventRepository.save(event);
//        }
        RequestStatus status = RequestStatus.PENDING;
        if (!event.getRequestModeration() || event.getParticipantLimit() == 0) {
            status = RequestStatus.CONFIRMED;
        }
        Request newParticipationRequest = Request.builder()
                .event(event)
                .requester(user)
                .createDate(LocalDateTime.now())
                .status(status)
                .build();
        return RequestMapper.toParticipationRequestDto(requestRepository.save(newParticipationRequest));
    }

    @Override
    public RequestDto cancelRequest(Long userId, Long requestId) {
        log.info("Cancellation of your request to participate in the event: user_id = {}, request_id = {}",
                userId, requestId);
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException(String.format(USER_NOT_FOUND.getValue(), userId)));
        Request requestToUpdate = requestRepository.getReferenceById(requestId);
        requestToUpdate.setStatus(RequestStatus.CANCELED);
        return RequestMapper.toParticipationRequestDto(requestRepository.save(requestToUpdate));
    }

    @Override
    public List<RequestDto> getAll(Long userId) {
        log.info("Getting information about the current user's requests to participate in other people's events: " +
                "user_id = {}", userId);
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException(String.format(USER_NOT_FOUND.getValue(), userId)));
        List<Optional<Request>> requests = requestRepository.findByRequesterId(userId);
        return requests.stream()
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(RequestMapper::toParticipationRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<RequestDto> getRequestsForUserEvent(Long userId, Long eventId) {
        log.info("Getting information about requests to participate in the event of the current user: " +
                "user_id = {}, event_id = {}", userId, eventId);
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException(String.format(USER_NOT_FOUND.getValue(), userId)));
        List<Event> userEvents = eventRepository.findByIdAndInitiatorId(eventId, userId);
        List<Optional<Request>> requests = requestRepository.findByEventIn(userEvents);
        return requests.stream()
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(RequestMapper::toParticipationRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public RequestStatusDto changeRequestsStatus(Long userId, Long eventId,
                                                 RequestStatusUpdateDto eventRequestStatusUpdateRequest) {
        log.info("Changing the status (confirmed, canceled) of applications for participation " +
                        "in the event of the current user: {}, event_id = {}, status = {}",
                userId, eventId, eventRequestStatusUpdateRequest.getStatus());
        userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException(String.format(USER_NOT_FOUND.getValue(), userId)));
        Event event = eventRepository.findById(eventId).orElseThrow(() ->
                new NotFoundException(String.format(EVENT_NOT_FOUND.getValue(), eventId)));
        List<Request> requests = requestRepository.findAllById(eventRequestStatusUpdateRequest.getRequestIds());
        RequestStatusDto eventRequestStatusUpdateResultDto = RequestStatusDto.builder()
                .confirmedRequests(new ArrayList<>())
                .rejectedRequests(new ArrayList<>())
                .build();
        if (!requests.isEmpty()) {
            if (eventRequestStatusUpdateRequest.getStatus() == RequestStatus.CONFIRMED) {
                int limitParticipants = event.getParticipantLimit();
                if (limitParticipants == 0 || !event.getRequestModeration()) {
                    throw new ValidationConflictException(String.format(REQUEST_FOR_UNPUBLISHED_EVENT.getValue()));
                }
                Integer countParticipants = requestRepository.countByEventIdAndStatus(event.getId(), RequestStatus.CONFIRMED);
                if (countParticipants == limitParticipants) {
                    throw new ValidationConflictException(String.format(REACHED_PARTICIPANT_LIMIT.getValue()));
                }
                for (Request request : requests) {
                    if (request.getStatus() != RequestStatus.PENDING) {
                        throw new ValidationConflictException(String.format(EVENT_MUST_BE_PENDING.getValue()));
                    }
                    if (countParticipants < limitParticipants) {
                        request.setStatus(RequestStatus.CONFIRMED);
                        eventRequestStatusUpdateResultDto.getConfirmedRequests().add(RequestMapper.toParticipationRequestDto(request));
                        countParticipants++;
                    } else {
                        request.setStatus(RequestStatus.REJECTED);
                        eventRequestStatusUpdateResultDto.getRejectedRequests().add(RequestMapper.toParticipationRequestDto(request));
                    }
                }
                requestRepository.saveAll(requests);
                if (countParticipants == limitParticipants) {
                    requestRepository.updateRequestStatusByEventIdAndStatus(event,
                            RequestStatus.PENDING, RequestStatus.REJECTED);
                }
            } else if (eventRequestStatusUpdateRequest.getStatus() == RequestStatus.REJECTED) {
                for (Request request : requests) {
                    if (request.getStatus() != RequestStatus.PENDING) {
                        throw new ValidationConflictException(String.format(EVENT_MUST_BE_PENDING.getValue()));
                    }
                    request.setStatus(RequestStatus.REJECTED);
                    eventRequestStatusUpdateResultDto.getRejectedRequests().add(RequestMapper.toParticipationRequestDto(request));
                }
                requestRepository.saveAll(requests);
            }
        }
        return eventRequestStatusUpdateResultDto;
    }
}