package ru.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.RequestDto;
import ru.practicum.dto.RequestStatusDto;
import ru.practicum.dto.RequestStatusUpdateDto;
import ru.practicum.service.RequestService;

import javax.validation.constraints.Positive;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Validated
@Slf4j
public class RequestController {
    private final RequestService requestService;

    @PostMapping("/users/{userId}/requests")
    @ResponseStatus(HttpStatus.CREATED)
    public RequestDto create(@PathVariable("userId") @Positive Long userId,
                             @RequestParam @Positive Long eventId) {
        log.info("POST request with userId = {}, eventId = {}", userId, eventId);
        return requestService.create(userId, eventId);
    }

    @PatchMapping("/users/{userId}/requests/{requestId}/cancel")
    public RequestDto cancelRequest(@PathVariable("userId") @Positive Long userId,
                                    @PathVariable("requestId") @Positive Long requestId) {
        log.info("PATCH request with userId = {}, requestId = {}", userId, requestId);
        return requestService.cancelRequest(userId, requestId);
    }

    @GetMapping("/users/{userId}/requests")
    public List<RequestDto> getAll(@PathVariable("userId") @Positive Long userId) {
        log.info("GET all requests with userId = {}", userId);
        return requestService.getAll(userId);
    }

    @GetMapping("/users/{userId}/events/{eventId}/requests")
    public List<RequestDto> getRequestsForUserEvent(@PathVariable("userId") @Positive Long userId,
                                                    @PathVariable("eventId") @Positive Long eventId) {
        log.info("GET requests with userId = {}, eventId = {}", userId, eventId);
        return requestService.getRequestsForUserEvent(userId, eventId);
    }

    @PatchMapping("/users/{userId}/events/{eventId}/requests")
    public RequestStatusDto changeRequestsStatus(@PathVariable("userId") @Positive Long userId,
                                                 @PathVariable("eventId") @Positive Long eventId,
                                                 @RequestBody RequestStatusUpdateDto eventRequestStatusUpdateRequest) {
        log.info("PATCH requests with userId = {}, eventId = {}, eventRequestStatusUpdateRequest = {}",
                userId, eventId, eventRequestStatusUpdateRequest);
        return requestService.changeRequestsStatus(userId, eventId, eventRequestStatusUpdateRequest);
    }
}