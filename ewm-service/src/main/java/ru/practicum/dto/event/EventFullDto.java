package ru.practicum.dto.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import ru.practicum.constant.FormatConstants;
import ru.practicum.dto.CategoryDto;
import ru.practicum.dto.LocationDto;
import ru.practicum.dto.UserDto;
import ru.practicum.dto.type.PublicationState;

import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class EventFullDto {
    private Long id;
    private String title;
    private String annotation;
    private CategoryDto category;
    private String description;
    @JsonFormat(pattern = FormatConstants.FORMAT_DATE_TIME)
    private LocalDateTime eventDate;
    private LocationDto location;
    private boolean paid;
    private int participantLimit;
    private boolean requestModeration;
    private Long confirmedRequests;
    @JsonFormat(pattern = FormatConstants.FORMAT_DATE_TIME)
    private LocalDateTime createdOn;
    @JsonFormat(pattern = FormatConstants.FORMAT_DATE_TIME)
    private LocalDateTime publishedOn;
    private UserDto initiator;
    private PublicationState state;
    private Long views;
}