package ru.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.CompilationCreateDto;
import ru.practicum.dto.CompilationDto;
import ru.practicum.dto.CompilationUpdateDto;
import ru.practicum.service.CompilationService;

import javax.validation.Valid;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@Validated
@RequiredArgsConstructor
@Slf4j
public class CompilationController {

    private final CompilationService compilationService;

    @PostMapping("/admin/compilations")
    @ResponseStatus(HttpStatus.CREATED)
    public CompilationDto create(@Valid @RequestBody CompilationCreateDto dto) {
        log.info("POST compilation = {}", dto);
        return compilationService.create(dto);
    }

    @PatchMapping("/admin/compilations/{compId}")
    public CompilationDto update(@PathVariable("compId") Long id, @Valid @RequestBody CompilationUpdateDto dto) {
        log.info("UPDATE compilation = {} with id = {}", dto, id);
        return compilationService.update(id, dto);
    }

    @DeleteMapping(value = "/admin/compilations/{compId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("compId") Long id) {
        log.info("DELETE compilation with id = {}", id);
        compilationService.delete(id);
    }

    @GetMapping("/compilations")
    public List<CompilationDto> getAll(@RequestParam(defaultValue = "false") Boolean pinned,
                                       @PositiveOrZero @RequestParam(defaultValue = "0")
                                       Integer from,
                                       @PositiveOrZero @RequestParam(defaultValue = "10")
                                       Integer size) {
        log.info("GET all compilations pinned = {}, from = {}, size = {}", pinned, from, size);
        return compilationService.getAll(pinned, from, size);
    }

    @GetMapping("/compilations/{compId}")
    public CompilationDto getById(@PathVariable("compId") Long id) {
        log.info("GET compilation with id = {}", id);
        return compilationService.getById(id);
    }
}