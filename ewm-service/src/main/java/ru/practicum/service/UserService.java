package ru.practicum.service;

import ru.practicum.dto.UserDto;

import java.util.List;

public interface UserService {

    UserDto create(UserDto userDto);

    void delete(Long id);

    List<UserDto> getAll(List<Long> ids, Integer from, Integer size);

    List<UserDto> getAll(Integer from, Integer size);
}