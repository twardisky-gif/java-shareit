package ru.practicum.shareit.request.mapper;

import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestItemDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Преобразование моделей и DTO для запросов вещей.
 */
public final class ItemRequestMapper {

    private ItemRequestMapper() {
    }

    public static ItemRequest toItemRequest(ItemRequestCreateDto createDto, User requestor, LocalDateTime created) {
        return ItemRequest.builder()
                .description(createDto.getDescription())
                .requestor(requestor)
                .created(created)
                .build();
    }

    public static ItemRequestDto toItemRequestDto(ItemRequest request, List<ItemRequestItemDto> items) {
        return ItemRequestDto.builder()
                .id(request.getId())
                .description(request.getDescription())
                .created(request.getCreated())
                .items(items)
                .build();
    }

    public static ItemRequestItemDto toItemRequestItemDto(Item item) {
        return ItemRequestItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .ownerId(item.getOwner().getId())
                .build();
    }
}
