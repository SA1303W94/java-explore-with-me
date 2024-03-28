package ru.practicum.dto.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import lombok.*;
import ru.practicum.constant.FormatConstants;
import ru.practicum.dto.LocationDto;
import ru.practicum.dto.type.EventAction;

import javax.validation.Valid;
import javax.validation.constraints.*;
import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class EventCreateDto {

    @NotBlank
    @Size(min = 3, max = 120)
    private String title;

    @NotBlank
    @Size(min = 20, max = 2000)
    private String annotation;

    @PositiveOrZero
    private long category;

    @NotBlank
    @Size(min = 20, max = 7000)
    private String description;

    @JsonFormat(pattern = FormatConstants.FORMAT_DATE_TIME)
    @NotNull
    @FutureOrPresent
    private LocalDateTime eventDate;

    @NotNull
    @Valid
    private LocationDto location;

    @JsonSetter(nulls = Nulls.SKIP)
    private Boolean paid = false;

    @JsonSetter(nulls = Nulls.SKIP)
    @PositiveOrZero
    private Integer participantLimit = 0;

    @JsonSetter(nulls = Nulls.SKIP)
    private Boolean requestModeration = true;

    private EventAction stateAction;
}