package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;

import java.util.List;

/**
 * Операции бизнес-логики для работы с пользователями.
 */
public interface UserService {

    /**
     * Создаёт пользователя.
     *
     * @param userCreateDto имя и адрес электронной почты пользователя
     * @return созданный пользователь
     */
    UserDto create(UserCreateDto userCreateDto);

    /**
     * Частично обновляет пользователя.
     *
     * @param userId идентификатор пользователя
     * @param userUpdateDto поля, которые нужно изменить
     * @return пользователь после изменения
     */
    UserDto update(Long userId, UserUpdateDto userUpdateDto);

    /**
     * Возвращает пользователя по идентификатору.
     *
     * @param userId идентификатор пользователя
     * @return найденный пользователь
     */
    UserDto getById(Long userId);

    /**
     * Возвращает всех пользователей.
     *
     * @return список пользователей
     */
    List<UserDto> getAll();

    /**
     * Удаляет пользователя по идентификатору.
     *
     * @param userId идентификатор пользователя
     */
    void deleteById(Long userId);
}
