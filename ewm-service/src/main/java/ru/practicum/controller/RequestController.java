package ru.practicum.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.RequestDto;
import ru.practicum.dto.RequestStatusDto;
import ru.practicum.dto.RequestStatusUpdateDto;
import ru.practicum.service.RequestService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Validated
public class RequestController {
    private final RequestService requestService;

    @PostMapping("/users/{userId}/requests")
    @ResponseStatus(HttpStatus.CREATED)
    public RequestDto create(@PathVariable("userId") @Valid @Positive Long userId,
                             @RequestParam @Valid @Positive Long eventId) {
        return requestService.create(userId, eventId);
    }

    @PatchMapping("/users/{userId}/requests/{requestId}/cancel")
    public RequestDto cancelRequest(@PathVariable("userId") @Valid @Positive Long userId,
                                    @PathVariable("requestId") @Valid @Positive Long requestId) {
        return requestService.cancelRequest(userId, requestId);
    }

    @GetMapping("/users/{userId}/requests")
    public List<RequestDto> getAll(@PathVariable("userId") @Valid @Positive Long userId) {
        return requestService.getAll(userId);
    }

    @GetMapping("/users/{userId}/events/{eventId}/requests")
    public List<RequestDto> getRequestsForUserEvent(@PathVariable("userId") @Valid @Positive Long userId,
                                                    @PathVariable("eventId") @Valid @Positive Long eventId) {
        return requestService.getRequestsForUserEvent(userId, eventId);
    }

    @PatchMapping("/users/{userId}/events/{eventId}/requests")
    public RequestStatusDto changeRequestsStatus(@PathVariable("userId") @Valid @Positive Long userId,
                                                 @PathVariable("eventId") @Valid @Positive Long eventId,
                                                 @RequestBody RequestStatusUpdateDto eventRequestStatusUpdateRequest) {
        return requestService.changeRequestsStatus(userId, eventId, eventRequestStatusUpdateRequest);
    }
}