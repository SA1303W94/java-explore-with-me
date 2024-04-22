package ru.practicum.service;

import ru.practicum.dto.request.RequestDto;
import ru.practicum.dto.request.RequestStatusDto;
import ru.practicum.dto.request.RequestStatusUpdateDto;

import java.util.List;

public interface RequestService {
    RequestDto create(Long userId, Long eventId);

    RequestDto cancelRequest(Long userId, Long requestId);

    List<RequestDto> getAll(Long userId);

    List<RequestDto> getRequestsForUserEvent(Long userId, Long eventId);

    RequestStatusDto changeRequestsStatus(Long userId, Long eventId,
                                          RequestStatusUpdateDto eventRequestStatusUpdateRequest);
}