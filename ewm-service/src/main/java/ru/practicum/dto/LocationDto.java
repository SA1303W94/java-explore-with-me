package ru.practicum.dto;

import lombok.*;

import javax.validation.constraints.NotNull;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class LocationDto {

    @NotNull
    private Double lat;

    @NotNull
    private Double lon;
}