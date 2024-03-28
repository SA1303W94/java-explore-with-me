package ru.practicum.dto.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
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
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;
    private long confirmedRequests;
    private UserDto initiator;
    private boolean paid;
    private long views;
}