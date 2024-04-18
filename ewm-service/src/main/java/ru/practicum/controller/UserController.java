package ru.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.UserDto;
import ru.practicum.groups.Create;
import ru.practicum.service.UserService;

import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping(path = "/admin/users")
@Validated
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto create(@RequestBody @Validated({Create.class}) UserDto userDto) {
        log.info("POST user = {}", userDto);
        return userService.create(userDto);
    }

    @DeleteMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("id") Long id) {
        log.info("DELETE user with id = {}", id);
        userService.delete(id);
    }

    @GetMapping
    public List<UserDto> getAll(@RequestParam(required = false) List<Long> ids,
                                @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                @PositiveOrZero @RequestParam(defaultValue = "10") Integer size) {
        log.info("GET all users ids = {}, from = {}, size = {}", ids, from, size);
        if (ids != null && !ids.isEmpty()) {
            return userService.getAll(ids, from, size);
        } else {
            return userService.getAll(from, size);
        }
    }
}