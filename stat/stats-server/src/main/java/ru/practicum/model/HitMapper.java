package ru.practicum.model;

import lombok.experimental.UtilityClass;
import org.springframework.stereotype.Component;
import ru.practicum.dto.HitDto;

@Component
@UtilityClass
public class HitMapper {

    public Hit toEntity(HitDto dto, Endpoint endpoint) {
        return Hit.builder()
                .endpoint(endpoint)
                .ipAddress(dto.getIp())
                .sentDttm(dto.getTimestamp())
                .build();
    }
}