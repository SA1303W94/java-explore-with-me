package ru.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.constant.FormatConstants;
import ru.practicum.dto.HitDto;
import ru.practicum.dto.StatDto;
import ru.practicum.service.StatServiceImpl;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@Validated
public class StatsController {

    private final StatServiceImpl statService;

    @PostMapping("/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public void create(@RequestBody @Valid HitDto hitDto) {
        log.info("Stat-server: POST hit={}", hitDto);
        statService.create(hitDto);
    }

    @GetMapping("/stats")
    public List<StatDto> getStats(@NotNull @PastOrPresent @DateTimeFormat(pattern = FormatConstants.DATE_TIME_PATTERN)
                                  @RequestParam LocalDateTime start,
                                  @NotNull @DateTimeFormat(pattern = FormatConstants.DATE_TIME_PATTERN)
                                  @RequestParam LocalDateTime end,
                                  @RequestParam(required = false) List<String> uris,
                                  @RequestParam(defaultValue = "false") Boolean unique) {
        log.info("Stat-server: GET stats start={}, end={}, uris={}, unique={}", start, end, uris, unique);
        return statService.getStats(start, end, uris, unique);
    }
}