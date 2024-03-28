package ru.practicum.service;

import ru.practicum.dto.CategoryDto;

import java.util.List;

public interface CategoryService {

    CategoryDto create(CategoryDto dto);

    CategoryDto update(Long id, CategoryDto dto);

    void delete(Long id);

    List<CategoryDto> getAll(Integer from, Integer size);

    CategoryDto getById(Long id);
}