package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.CommentCreateDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemBookingsDto;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;

import java.util.List;

/**
 * REST-контракт для работы с вещами, которые пользователи предлагают для аренды.
 */
public interface ItemController {

    /**
     * Добавляет вещь. Её владельцем становится пользователь из заголовка.
     *
     * @param ownerId идентификатор владельца из заголовка X-Sharer-User-Id
     * @param itemCreateDto данные новой вещи
     * @return созданная вещь с присвоенным идентификатором
     */
    ItemDto create(Long ownerId, ItemCreateDto itemCreateDto);

    /**
     * Частично обновляет вещь. Доступно только владельцу вещи.
     *
     * @param ownerId идентификатор владельца из заголовка X-Sharer-User-Id
     * @param itemId идентификатор изменяемой вещи
     * @param itemUpdateDto поля, которые нужно изменить
     * @return вещь после изменения
     */
    ItemDto update(Long ownerId, Long itemId, ItemUpdateDto itemUpdateDto);

    /**
     * Возвращает вещь с отзывами. Даты бронирований заполняются только для владельца.
     *
     * @param userId идентификатор пользователя из заголовка X-Sharer-User-Id
     * @param itemId идентификатор вещи
     * @return найденная вещь
     */
    ItemBookingsDto getById(Long userId, Long itemId);

    /**
     * Возвращает вещи владельца с датами ближайших бронирований и отзывами.
     *
     * @param ownerId идентификатор владельца из заголовка X-Sharer-User-Id
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
     * @param userId идентификатор автора отзыва из заголовка X-Sharer-User-Id
     * @param itemId идентификатор вещи
     * @param commentCreateDto текст отзыва
     * @return сохранённый отзыв
     */
    CommentDto addComment(Long userId, Long itemId, CommentCreateDto commentCreateDto);
}
