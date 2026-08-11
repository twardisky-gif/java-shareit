package ru.practicum.shareit.request;

import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

/**
 * REST-контракт для работы с запросами вещей.
 */
public interface ItemRequestController {

    /**
     * Создаёт запрос на необходимую вещь.
     *
     * @param requestorId идентификатор автора запроса из заголовка X-Sharer-User-Id
     * @param createDto описание необходимой вещи
     * @return созданный запрос
     */
    ItemRequestDto create(Long requestorId, ItemRequestCreateDto createDto);

    /**
     * Возвращает запросы текущего пользователя от новых к старым.
     *
     * @param requestorId идентификатор автора запросов из заголовка X-Sharer-User-Id
     * @return список запросов пользователя
     */
    List<ItemRequestDto> getOwn(Long requestorId);

    /**
     * Возвращает запросы других пользователей от новых к старым.
     *
     * @param userId идентификатор текущего пользователя из заголовка X-Sharer-User-Id
     * @return список запросов других пользователей
     */
    List<ItemRequestDto> getOtherUsersRequests(Long userId);

    /**
     * Возвращает запрос по идентификатору.
     *
     * @param userId идентификатор текущего пользователя из заголовка X-Sharer-User-Id
     * @param requestId идентификатор запроса
     * @return найденный запрос
     */
    ItemRequestDto getById(Long userId, Long requestId);
}
