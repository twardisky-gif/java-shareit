package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;

import java.util.List;

public interface ItemService {

    ItemDto create(Long ownerId, ItemCreateDto itemCreateDto);

    ItemDto update(Long ownerId, Long itemId, ItemUpdateDto itemUpdateDto);

    ItemDto getById(Long itemId);

    List<ItemDto> getByOwnerId(Long ownerId);

    List<ItemDto> search(String text);
}
