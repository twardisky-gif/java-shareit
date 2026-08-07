package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemStorage itemStorage;
    private final UserStorage userStorage;

    @Override
    public ItemDto create(Long ownerId, ItemCreateDto itemCreateDto) {
        User owner = requireUser(ownerId);
        Item saved = itemStorage.save(ItemMapper.toItem(itemCreateDto, owner));
        return ItemMapper.toItemDto(saved);
    }

    @Override
    public ItemDto update(Long ownerId, Long itemId, ItemUpdateDto itemUpdateDto) {
        User owner = requireUser(ownerId);
        Item stored = requireItem(itemId);
        if (!stored.getOwner().getId().equals(owner.getId())) {
            throw new NotFoundException("Вещь с id " + itemId + " не принадлежит пользователю " + ownerId);
        }
        Item updated = Item.builder()
                .id(stored.getId())
                .name(itemUpdateDto.getName() == null ? stored.getName() : itemUpdateDto.getName())
                .description(itemUpdateDto.getDescription() == null
                        ? stored.getDescription()
                        : itemUpdateDto.getDescription())
                .available(itemUpdateDto.getAvailable() == null ? stored.getAvailable() : itemUpdateDto.getAvailable())
                .owner(stored.getOwner())
                .request(stored.getRequest())
                .build();
        return ItemMapper.toItemDto(itemStorage.save(updated));
    }

    @Override
    public ItemDto getById(Long itemId) {
        return ItemMapper.toItemDto(requireItem(itemId));
    }

    @Override
    public List<ItemDto> getByOwnerId(Long ownerId) {
        requireUser(ownerId);
        return itemStorage.findByOwnerId(ownerId).stream()
                .map(ItemMapper::toItemDto)
                .toList();
    }

    @Override
    public List<ItemDto> search(String text) {
        if (text == null || text.isBlank()) {
            return List.of();
        }
        return itemStorage.findAvailableByText(text).stream()
                .map(ItemMapper::toItemDto)
                .toList();
    }

    private User requireUser(Long userId) {
        return userStorage.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден"));
    }

    private Item requireItem(Long itemId) {
        return itemStorage.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь с id " + itemId + " не найдена"));
    }
}
