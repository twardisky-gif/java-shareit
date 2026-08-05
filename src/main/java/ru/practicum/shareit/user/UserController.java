package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;

import java.util.List;

/**
 * REST-контракт для работы с пользователями сервиса.
 */
public interface UserController {

    /**
     * Создаёт нового пользователя.
     *
     * @param userCreateDto данные нового пользователя
     * @return созданный пользователь с присвоенным идентификатором
     */
    UserDto create(UserCreateDto userCreateDto);

    /**
     * Частично обновляет пользователя: переданными считаются только непустые поля.
     *
     * @param userId идентификатор изменяемого пользователя
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
     * Возвращает всех зарегистрированных пользователей.
     *
     * @return список пользователей
     */
    List<UserDto> getAll();

    /**
     * Удаляет пользователя вместе с возможностью использовать его идентификатор.
     *
     * @param userId идентификатор удаляемого пользователя
     */
    void deleteById(Long userId);
}
