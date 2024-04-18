package ru.practicum.dto.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import ru.practicum.constant.FormatConstants;
import ru.practicum.dto.CategoryDto;
import ru.practicum.dto.UserDto;

import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@RequiredArgsConstructor
@Getter
@Setter
public class EventShortDto {
    private Long id;
    private String title;
    private String annotation;
    private CategoryDto category;
    @JsonFormat(pattern = FormatConstants.FORMAT_DATE_TIME)
    private LocalDateTime eventDate;
    private UserDto initiator;
    private boolean paid;
    private Long views;
    private Long confirmedRequests;
}