package ru.practicum.model;

import lombok.experimental.UtilityClass;
import org.springframework.stereotype.Component;
import ru.practicum.dto.HitDto;
import ru.practicum.dto.StatDto;

@Component
@UtilityClass
public class EndpointMapper {
    public Endpoint toEntity(HitDto dto) {
        return Endpoint.builder()
                .appName(dto.getApp())
                .uri(dto.getUri())
                .build();
    }

    public StatDto toDto(Endpoint e, Long hitsCount) {
        return StatDto.builder()
                .app(e.getAppName())
                .uri(e.getUri())
                .hits(hitsCount)
                .build();
    }
}