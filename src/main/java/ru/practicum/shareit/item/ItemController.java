package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;

import java.util.List;

/**
 * REST-контракт для работы с вещами, которые пользователи предлагают для аренды.
 */
public interface ItemController {

    String USER_ID_HEADER = "X-Sharer-User-Id";

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
     * Возвращает вещь по идентификатору. Доступно любому пользователю.
     *
     * @param itemId идентификатор вещи
     * @return найденная вещь
     */
    ItemDto getById(Long itemId);

    /**
     * Возвращает все вещи владельца.
     *
     * @param ownerId идентификатор владельца из заголовка X-Sharer-User-Id
     * @return список вещей владельца
     */
    List<ItemDto> getByOwnerId(Long ownerId);

    /**
     * Ищет доступные для аренды вещи по вхождению текста в название или описание.
     *
     * @param text искомый текст, регистр не учитывается
     * @return список доступных вещей, пустой список при пустом запросе
     */
    List<ItemDto> search(String text);
}
