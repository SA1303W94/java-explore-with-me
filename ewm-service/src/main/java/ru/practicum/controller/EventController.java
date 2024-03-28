package ru.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.constant.FormatConstants;
import ru.practicum.dto.event.*;
import ru.practicum.service.EventService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Validated
@Slf4j
public class EventController {
    private final EventService eventService;

    @PostMapping("/users/{userId}/events")
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto create(@PathVariable("userId") @Positive Long userId,
                               @Valid @RequestBody EventCreateDto eventDto) {
        log.info("POST event = {} with userId = {}", eventDto, userId);
        return eventService.createEvent(userId, eventDto);
    }

    @GetMapping("/users/{userId}/events")
    public List<EventShortDto> getByUserId(@PathVariable("userId") @Positive Long userId,
                                           @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                           @RequestParam(defaultValue = "10") @Positive Integer size) {
        log.info("GET events with userId = {}, from = {}, size = {}", userId, from, size);
        return eventService.getEvents(userId, from, size);
    }

    @GetMapping("/users/{userId}/events/{eventId}")
    public EventFullDto getById(@PathVariable("userId") @Positive Long userId,
                                @PathVariable("eventId") @Positive Long eventId) {
        log.info("GET event with userId = {}, eventId = {}", userId, eventId);
        return eventService.getEventById(userId, eventId);
    }

    @PatchMapping("/users/{userId}/events/{eventId}")
    public EventFullDto updateEventByUser(@PathVariable("userId") @Positive Long userId,
                                          @PathVariable("eventId") @Positive Long eventId,
                                          @RequestBody @Validated EventUpdateUserDto updateEventUserRequestDto) {
        log.info("PATCH event with userId = {}, eventId = {}, updateEventUserRequestDto = {}", userId, eventId, updateEventUserRequestDto);
        return eventService.updateEventByUser(userId, eventId, updateEventUserRequestDto);
    }

    @GetMapping("/admin/events")
    public List<EventFullDto> getEventsByAdmin(@RequestParam(required = false) List<Long> users,
                                               @RequestParam(required = false) List<String> states,
                                               @RequestParam(required = false) List<Long> categories,
                                               @DateTimeFormat(pattern = FormatConstants.FORMAT_DATE_TIME)
                                               @RequestParam(required = false) LocalDateTime rangeStart,
                                               @DateTimeFormat(pattern = FormatConstants.FORMAT_DATE_TIME)
                                               @RequestParam(required = false) LocalDateTime rangeEnd,
                                               @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                               @RequestParam(defaultValue = "10") @Positive int size) {
        log.info("GET all events from = {}, size = {}, users = {}, states = {}, categories = {}, " +
                "rangeStart = {}, rangeEnd = {}", from, size, users, states, categories, rangeStart, rangeEnd);
        return eventService.getEventsByAdmin(users, states, categories, rangeStart, rangeEnd, from, size);
    }

    @PatchMapping("/admin/events/{eventId}")
    public EventFullDto updateEventByAdmin(@PathVariable("eventId") @Positive Long eventId,
                                           @RequestBody @Valid EventUpdateAdminDto updateEventAdminRequestDto) {
        log.info("PATCH event with eventId = {}, updateEventAdminRequestDto = {}", eventId, updateEventAdminRequestDto);
        return eventService.updateEventByAdmin(eventId, updateEventAdminRequestDto);
    }

    @GetMapping("/events")
    public List<EventShortDto> getPublishedEvents(@RequestParam(required = false) String text,
                                                  @RequestParam(required = false) List<Long> categories,
                                                  @RequestParam(required = false) Boolean paid,
                                                  @DateTimeFormat(pattern = FormatConstants.FORMAT_DATE_TIME)
                                                  @RequestParam(required = false) LocalDateTime rangeStart,
                                                  @DateTimeFormat(pattern = FormatConstants.FORMAT_DATE_TIME)
                                                  @RequestParam(required = false) LocalDateTime rangeEnd,
                                                  @RequestParam(defaultValue = "false") boolean onlyAvailable,
                                                  @RequestParam(required = false) String sort,
                                                  @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                                  @RequestParam(defaultValue = "10") @Positive int size,
                                                  HttpServletRequest request) {
        log.info("GET all published events from = {}, size = {}, text = {}, categories = {}, paid = {}, " +
                        "rangeStart = {}, rangeEnd = {}, onlyAvailable = {}, sort = {}, request = {}",
                from, size, text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, request);
        return eventService.getPublishedEvents(text, categories, paid, rangeStart, rangeEnd, onlyAvailable,
                sort, from, size, request);
    }

    @GetMapping("/events/{eventId}")
    public EventFullDto getPublishedEventById(@PathVariable("eventId") @Positive Long eventId,
                                              HttpServletRequest request) {
        log.info("GET event with eventId = {}, request = {}", eventId, request);
        return eventService.getPublishedEventById(eventId, request);
    }
}