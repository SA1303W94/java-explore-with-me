package ru.practicum.service;

import ru.practicum.dto.compilation.CompilationCreateDto;
import ru.practicum.dto.compilation.CompilationDto;
import ru.practicum.dto.compilation.CompilationUpdateDto;

import java.util.List;

public interface CompilationService {

    List<CompilationDto> getAll(Boolean pinned, int from, int size);

    CompilationDto getById(Long compId);

    CompilationDto create(CompilationCreateDto newCompilationDto);

    CompilationDto update(Long compId, CompilationUpdateDto updateCompilationRequestDto);

    void delete(Long compId);
}