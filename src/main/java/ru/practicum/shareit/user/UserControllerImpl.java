package ru.practicum.shareit.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserControllerImpl implements UserController {

    private final UserService userService;

    @Override
    @PostMapping
    public UserDto create(@Valid @RequestBody UserCreateDto userCreateDto) {
        log.info("Создание пользователя {}", userCreateDto.getName());
        return userService.create(userCreateDto);
    }

    @Override
    @PatchMapping("/{userId}")
    public UserDto update(@PathVariable Long userId, @Valid @RequestBody UserUpdateDto userUpdateDto) {
        log.info("Обновление пользователя {}", userId);
        return userService.update(userId, userUpdateDto);
    }

    @Override
    @GetMapping("/{userId}")
    public UserDto getById(@PathVariable Long userId) {
        return userService.getById(userId);
    }

    @Override
    @GetMapping
    public List<UserDto> getAll() {
        return userService.getAll();
    }

    @Override
    @DeleteMapping("/{userId}")
    public void deleteById(@PathVariable Long userId) {
        log.info("Удаление пользователя {}", userId);
        userService.deleteById(userId);
    }
}
