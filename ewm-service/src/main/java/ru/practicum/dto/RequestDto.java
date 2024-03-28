package ru.practicum.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import ru.practicum.constant.FormatConstants;
import ru.practicum.dto.type.RequestStatus;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RequestDto {

    private Long id;

    @JsonFormat(pattern = FormatConstants.FORMAT_DATE_TIME)
    private LocalDateTime created;

    private Long event;

    private Long requester;

    private RequestStatus status;
}