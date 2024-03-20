package ru.practicum.controller;

import lombok.RequiredArgsConstructor;
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
public class CompilationController {

    private final CompilationService compilationService;

    @PostMapping("/admin/compilations")
    @ResponseStatus(HttpStatus.CREATED)
    public CompilationDto create(@Valid @RequestBody CompilationCreateDto dto) {
        return compilationService.create(dto);
    }

    @PatchMapping("/admin/compilations/{compId}")
    public CompilationDto update(@PathVariable("compId") Long id, @Valid @RequestBody CompilationUpdateDto dto) {
        return compilationService.update(id, dto);
    }

    @DeleteMapping(value = "/admin/compilations/{compId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("compId") Long id) {
        compilationService.delete(id);
    }

    @GetMapping("/compilations")
    public List<CompilationDto> getAll(@RequestParam(required = false, defaultValue = "false") Boolean pinned,
                                       @Valid @PositiveOrZero @RequestParam(required = false, defaultValue = "0")
                                       Integer from,
                                       @Valid @PositiveOrZero @RequestParam(required = false, defaultValue = "10")
                                       Integer size) {
        return compilationService.getAll(pinned, from, size);
    }

    @GetMapping("/compilations/{compId}")
    public CompilationDto getById(@PathVariable("compId") Long id) {
        return compilationService.getById(id);
    }
}