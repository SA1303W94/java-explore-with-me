package ru.practicum.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.dto.LocationDto;
import ru.practicum.model.Location;

@UtilityClass
public class LocationMapper {

    public LocationDto mapToDto(Location location) {
        return LocationDto.builder()
                .lat(location.getLatitude())
                .lon(location.getLongitude())
                .build();
    }

    public Location mapToEntity(LocationDto dto) {
        return Location.builder()
                .latitude(dto.getLat())
                .longitude(dto.getLon())
                .build();
    }
}