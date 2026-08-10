package ru.practicum.shareit.request;

import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

/**
 * REST-контракт для работы с запросами вещей.
 */
public interface ItemRequestController {

    ItemRequestDto create(Long requestorId, ItemRequestCreateDto createDto);

    List<ItemRequestDto> getOwn(Long requestorId);

    List<ItemRequestDto> getOtherUsersRequests(Long userId);

    ItemRequestDto getById(Long userId, Long requestId);
}
