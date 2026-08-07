package ru.practicum.shareit.item.storage;

import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemStorage {

    Item save(Item item);

    Optional<Item> findById(Long itemId);

    List<Item> findByOwnerId(Long ownerId);

    List<Item> findAvailableByText(String text);

    void deleteByOwnerId(Long ownerId);
}
