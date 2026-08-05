package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserStorage userStorage;

    @Override
    public UserDto create(UserCreateDto userCreateDto) {
        requireEmailNotTaken(userCreateDto.getEmail(), null);
        User saved = userStorage.save(UserMapper.toUser(userCreateDto));
        return UserMapper.toUserDto(saved);
    }

    @Override
    public UserDto update(Long userId, UserUpdateDto userUpdateDto) {
        User stored = requireUser(userId);
        String email = userUpdateDto.getEmail() == null ? stored.getEmail() : userUpdateDto.getEmail();
        String name = userUpdateDto.getName() == null ? stored.getName() : userUpdateDto.getName();
        requireEmailNotTaken(email, userId);
        User updated = User.builder()
                .id(stored.getId())
                .name(name)
                .email(email)
                .build();
        return UserMapper.toUserDto(userStorage.save(updated));
    }

    @Override
    public UserDto getById(Long userId) {
        return UserMapper.toUserDto(requireUser(userId));
    }

    @Override
    public List<UserDto> getAll() {
        return userStorage.findAll().stream()
                .map(UserMapper::toUserDto)
                .toList();
    }

    @Override
    public void deleteById(Long userId) {
        requireUser(userId);
        userStorage.deleteById(userId);
    }

    private User requireUser(Long userId) {
        return userStorage.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден"));
    }

    private void requireEmailNotTaken(String email, Long ownerId) {
        userStorage.findByEmail(email)
                .filter(existing -> !existing.getId().equals(ownerId))
                .ifPresent(existing -> {
                    throw new ConflictException("Электронная почта " + email + " уже используется");
                });
    }
}
