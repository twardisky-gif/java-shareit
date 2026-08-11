package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

/**
 * Операции над запросами вещей.
 */
public interface ItemRequestService {

    /**
     * Создаёт запрос на необходимую вещь.
     *
     * @param requestorId идентификатор автора запроса
     * @param createDto описание необходимой вещи
     * @return созданный запрос
     */
    ItemRequestDto create(Long requestorId, ItemRequestCreateDto createDto);

    /**
     * Возвращает запросы пользователя от новых к старым.
     *
     * @param requestorId идентификатор автора запросов
     * @return список запросов пользователя
     */
    List<ItemRequestDto> getOwn(Long requestorId);

    /**
     * Возвращает запросы других пользователей от новых к старым.
     *
     * @param userId идентификатор текущего пользователя
     * @return список запросов других пользователей
     */
    List<ItemRequestDto> getOtherUsersRequests(Long userId);

    /**
     * Возвращает запрос по идентификатору.
     *
     * @param userId идентификатор текущего пользователя
     * @param requestId идентификатор запроса
     * @return найденный запрос
     */
    ItemRequestDto getById(Long userId, Long requestId);
}
