package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    @Transactional
    public UserDto create(UserCreateDto userCreateDto) {
        requireEmailNotTaken(userCreateDto.getEmail(), null);
        User saved = userRepository.save(UserMapper.toUser(userCreateDto));
        return UserMapper.toUserDto(saved);
    }

    @Override
    @Transactional
    public UserDto update(Long userId, UserUpdateDto userUpdateDto) {
        User stored = requireUser(userId);
        String email = userUpdateDto.getEmail() == null ? stored.getEmail() : userUpdateDto.getEmail();
        String name = userUpdateDto.getName() == null ? stored.getName() : userUpdateDto.getName();
        requireEmailNotTaken(email, userId);
        stored.setName(name);
        stored.setEmail(email);
        return UserMapper.toUserDto(userRepository.save(stored));
    }

    @Override
    public UserDto getById(Long userId) {
        return UserMapper.toUserDto(requireUser(userId));
    }

    @Override
    public List<UserDto> getAll() {
        return userRepository.findAll().stream()
                .map(UserMapper::toUserDto)
                .toList();
    }

    @Override
    @Transactional
    public void deleteById(Long userId) {
        requireUser(userId);
        userRepository.deleteById(userId);
    }

    private User requireUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден"));
    }

    private void requireEmailNotTaken(String email, Long ownerId) {
        userRepository.findByEmail(email)
                .filter(existing -> !existing.getId().equals(ownerId))
                .ifPresent(existing -> {
                    throw new ConflictException("Электронная почта " + email + " уже используется");
                });
    }
}
