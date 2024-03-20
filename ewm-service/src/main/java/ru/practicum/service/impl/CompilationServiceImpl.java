package ru.practicum.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.CompilationCreateDto;
import ru.practicum.dto.CompilationDto;
import ru.practicum.dto.CompilationUpdateDto;
import ru.practicum.exception.NotFoundException;
import ru.practicum.mapper.CompilationMapper;
import ru.practicum.model.Compilation;
import ru.practicum.repository.CompilationRepository;
import ru.practicum.repository.EventRepository;
import ru.practicum.service.CompilationService;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.exception.ExceptionType.COMPILATION_NOT_FOUND;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class CompilationServiceImpl implements CompilationService {

    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;

    @Override
    public List<CompilationDto> getAll(Boolean pinned, int from, int size) {
        log.info("Getting collections of events by parameters: pinned = " + pinned + ", from = " + from + ", size = " + size);
        List<Compilation> compilations;
        // if (pinned != null) {
        compilations = compilationRepository.findByPinnedEquals(pinned, PageRequest.of(from / size, size));
        //  } else {
        // compilations = compilationRepository.findAll(PageRequest.of(from / size, size)).getContent();
        //  }
        return !compilations.isEmpty() ? compilations.stream().map(CompilationMapper::toCompilationDto)
                .collect(Collectors.toList()) : Collections.emptyList();
    }

    @Override
    public CompilationDto getById(Long id) {
        log.info("Getting a selection of events by ID = " + id);
        return CompilationMapper.toCompilationDto(compilationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format(COMPILATION_NOT_FOUND.getValue(), id))));
    }

    @Override
    public CompilationDto create(CompilationCreateDto newCompilationDto) {
        log.info("Adding a new collection: compilation = " + newCompilationDto);
        Compilation compilation = CompilationMapper.mapToEntity(newCompilationDto);
        if (newCompilationDto.getEvents() != null) {
            compilation.setEvents(eventRepository.findByIdIn(newCompilationDto.getEvents()));
        }
        return CompilationMapper.toCompilationDto(compilationRepository.save(compilation));
    }

    @Override
    public CompilationDto update(Long id, CompilationUpdateDto updateCompilationRequestDto) {
        log.info("Updating information about compilation: comp_id = " + id + ", update_compilation = " + updateCompilationRequestDto);
        Compilation compilation = compilationRepository.findById(id).orElseThrow(() ->
                new NotFoundException(String.format(COMPILATION_NOT_FOUND.getValue(), id)));
        if (updateCompilationRequestDto.getTitle() != null) {
            compilation.setTitle(updateCompilationRequestDto.getTitle());
        }
        if (updateCompilationRequestDto.getPinned() != null) {
            compilation.setPinned(updateCompilationRequestDto.getPinned());
        }
        if (updateCompilationRequestDto.getEvents() != null) {
            compilation.setEvents(eventRepository.findByIdIn(updateCompilationRequestDto.getEvents()));
        }
        return CompilationMapper.toCompilationDto(compilationRepository.save(compilation));
    }

    @Override
    public void delete(Long id) {
        log.info("Deleting compilation: comp_id = " + id);
        compilationRepository.findById(id).orElseThrow(() ->
                new NotFoundException(String.format(COMPILATION_NOT_FOUND.getValue(), id)));
        compilationRepository.deleteById(id);
    }
}