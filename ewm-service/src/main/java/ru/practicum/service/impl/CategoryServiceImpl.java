package ru.practicum.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.CategoryDto;
import ru.practicum.exception.NotFoundException;
import ru.practicum.mapper.CategoryMapper;
import ru.practicum.model.Category;
import ru.practicum.page.CustomPageRequest;
import ru.practicum.repository.CategoryRepository;
import ru.practicum.repository.EventRepository;
import ru.practicum.service.CategoryService;

import java.util.List;
import java.util.NoSuchElementException;

import static ru.practicum.exception.ExceptionType.CATEGORY_HAS_EVENTS;
import static ru.practicum.exception.ExceptionType.CATEGORY_NOT_FOUND;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final EventRepository eventRepository;

    @Override
    public CategoryDto create(CategoryDto dto) {
        Category category = categoryRepository.save(CategoryMapper.mapToEntity(dto));
        log.info("new category {} has been created", category);
        return CategoryMapper.mapToDto(category);
    }

    @Override
    public CategoryDto update(Long id, CategoryDto dto) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format(CATEGORY_NOT_FOUND.getValue(), id)));
        String name = dto.getName();

        category.setName(name);
        log.info("updating category by id = {}, name = {}", id, name);
        return CategoryMapper.mapToDto(categoryRepository.save(category));
    }

    @Override
    public void delete(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format(CATEGORY_NOT_FOUND.getValue(), id)));

        log.info("deleting category by id = {}", id);

        if (eventRepository.findFirstByCategoryId(id) != null) {
            throw new SecurityException(String.format(CATEGORY_HAS_EVENTS.getValue(), id));
        }
        categoryRepository.delete(category);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryDto> getAll(Integer from, Integer size) {
        Pageable pageable = CustomPageRequest.of(from, size);
        List<Category> categories = categoryRepository.findAll(pageable).getContent();
        log.info("found {} categories", categories.size());
        return CategoryMapper.mapToDtos(categories);
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryDto getById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException(String.format(CATEGORY_NOT_FOUND.getValue(), id)));
        log.info("found category: {}", category);
        return CategoryMapper.mapToDto(category);
    }
}