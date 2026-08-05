package ru.practicum.shareit.item.storage;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

@Repository
public class InMemoryItemStorage implements ItemStorage {

    private final Map<Long, Item> items = new HashMap<>();
    private final Map<Long, List<Item>> itemsByOwnerId = new HashMap<>();
    private long lastGeneratedId;

    @Override
    public Item save(Item item) {
        if (item.getId() == null) {
            item.setId(++lastGeneratedId);
            itemsByOwnerId.computeIfAbsent(item.getOwner().getId(), ownerId -> new ArrayList<>()).add(item);
        } else {
            List<Item> ownerItems = itemsByOwnerId.getOrDefault(item.getOwner().getId(), new ArrayList<>());
            ownerItems.replaceAll(stored -> stored.getId().equals(item.getId()) ? item : stored);
        }
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public Optional<Item> findById(Long itemId) {
        return Optional.ofNullable(items.get(itemId));
    }

    @Override
    public List<Item> findByOwnerId(Long ownerId) {
        return new ArrayList<>(itemsByOwnerId.getOrDefault(ownerId, List.of()));
    }

    @Override
    public List<Item> findAvailableByText(String text) {
        String query = text.toLowerCase(Locale.ROOT);
        return items.values().stream()
                .filter(item -> Boolean.TRUE.equals(item.getAvailable()))
                .filter(item -> matches(item, query))
                .toList();
    }

    private boolean matches(Item item, String query) {
        return item.getName().toLowerCase(Locale.ROOT).contains(query)
                || item.getDescription().toLowerCase(Locale.ROOT).contains(query);
    }
}
