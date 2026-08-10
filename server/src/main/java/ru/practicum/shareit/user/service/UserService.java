package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;

import java.util.List;

public interface UserService {

    UserDto create(UserCreateDto userCreateDto);

    UserDto update(Long userId, UserUpdateDto userUpdateDto);

    UserDto getById(Long userId);

    List<UserDto> getAll();

    void deleteById(Long userId);
}
