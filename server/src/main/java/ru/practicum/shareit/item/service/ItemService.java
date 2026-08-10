package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentCreateDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemBookingsDto;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;

import java.util.List;

/**
 * Операции над вещами, которые пользователи предлагают для аренды.
 */
public interface ItemService {

    /**
     * Добавляет вещь и назначает её владельцем указанного пользователя.
     *
     * @param ownerId идентификатор владельца
     * @param itemCreateDto данные новой вещи
     * @return созданная вещь с присвоенным идентификатором
     */
    ItemDto create(Long ownerId, ItemCreateDto itemCreateDto);

    /**
     * Частично обновляет вещь. Доступно только владельцу.
     *
     * @param ownerId идентификатор владельца
     * @param itemId идентификатор изменяемой вещи
     * @param itemUpdateDto поля, которые нужно изменить
     * @return вещь после изменения
     */
    ItemDto update(Long ownerId, Long itemId, ItemUpdateDto itemUpdateDto);

    /**
     * Возвращает вещь с комментариями. Даты бронирований заполняются только для владельца.
     *
     * @param userId идентификатор запрашивающего пользователя
     * @param itemId идентификатор вещи
     * @return найденная вещь
     */
    ItemBookingsDto getById(Long userId, Long itemId);

    /**
     * Возвращает вещи владельца вместе с датами ближайших бронирований и комментариями.
     *
     * @param ownerId идентификатор владельца
     * @return список вещей владельца
     */
    List<ItemBookingsDto> getByOwnerId(Long ownerId);

    /**
     * Ищет доступные для аренды вещи по вхождению текста в название или описание.
     *
     * @param text искомый текст, регистр не учитывается
     * @return список доступных вещей, пустой список при пустом запросе
     */
    List<ItemDto> search(String text);

    /**
     * Добавляет отзыв о вещи. Доступно пользователю, у которого завершилась аренда этой вещи.
     *
     * @param userId идентификатор автора отзыва
     * @param itemId идентификатор вещи
     * @param commentCreateDto текст отзыва
     * @return сохранённый отзыв
     */
    CommentDto addComment(Long userId, Long itemId, CommentCreateDto commentCreateDto);
}
