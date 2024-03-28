package ru.practicum.service;

import ru.practicum.dto.event.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface EventService {
    EventFullDto createEvent(Long userId, EventCreateDto newEventDto);

    List<EventShortDto> getEvents(Long userId, int from, int size);

    EventFullDto getEventById(Long userId, Long eventId);

    EventFullDto updateEventByUser(Long userId, Long eventId, EventUpdateUserDto updateEventUserRequestDto);

    EventFullDto updateEventByAdmin(Long eventId, EventUpdateAdminDto updateEventAdminRequestDto);

    List<EventFullDto> getEventsByAdmin(List<Long> users, List<String> states, List<Long> categories, String rangeStart,
                                        String rangeEnd, int from, int size);

    List<EventShortDto> getPublishedEvents(String text, List<Long> categories, Boolean paid, String rangeStart, String rangeEnd,
                                           boolean onlyAvailable, String sort, int from, int size, HttpServletRequest request);

    EventFullDto getPublishedEventById(Long eventId, HttpServletRequest request);
}