package ru.practicum.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.dto.UserDto;
import ru.practicum.model.User;

import java.util.List;
import java.util.stream.Collectors;

@UtilityClass
public class UserMapper {

    public User mapToEntity(UserDto dto) {
        return User.builder()
                // .id(dto.getId())
                .name(dto.getName())
                .email(dto.getEmail())
                .build();
    }

    public UserDto mapToDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }

    public List<UserDto> mapToDtos(List<User> users) {
        return users.stream().map(UserMapper::mapToDto).collect(Collectors.toList());
    }

    public UserDto mapToUserShortDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .build();
    }
}