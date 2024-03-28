package ru.practicum.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.UserDto;
import ru.practicum.exception.NotFoundException;
import ru.practicum.mapper.UserMapper;
import ru.practicum.model.User;
import ru.practicum.repository.UserRepository;
import ru.practicum.service.UserService;

import java.util.List;

import static ru.practicum.exception.ExceptionType.USER_NOT_FOUND;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public UserDto create(UserDto dto) {
        User user = userRepository.save(UserMapper.mapToEntity(dto));
        log.info("new user has been created. id = {}, email = {}", user.getId(), user.getEmail());
        return UserMapper.mapToDto(user);
    }

    @Override
    public void delete(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format(USER_NOT_FOUND.getValue(), id)));
        userRepository.delete(user);
        log.info("user with id = {} has been deleted", id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> getAll(List<Long> ids, Integer from, Integer size) {
        log.info("Getting a list of users by IDs: ids = {}, from = {}, size = {}", ids, from, size);
        return UserMapper.mapToDtos(userRepository.findByIdIn(ids, PageRequest.of(from / size, size)));
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> getAll(Integer from, Integer size) {
        log.info("Getting a list of all users: from = {}, size = {}", from, size);
        return UserMapper.mapToDtos(userRepository.findAll(PageRequest.of(from / size, size)).getContent());
    }
}