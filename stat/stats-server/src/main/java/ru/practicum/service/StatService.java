package ru.practicum.service;

import ru.practicum.dto.*;

import java.time.LocalDateTime;
import java.util.List;

public interface StatService {

    void create(HitDto hitDto);

    List<StatDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique);
}