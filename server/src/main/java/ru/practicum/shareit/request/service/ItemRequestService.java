package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

/**
 * Операции над запросами вещей.
 */
public interface ItemRequestService {

    ItemRequestDto create(Long requestorId, ItemRequestCreateDto createDto);

    List<ItemRequestDto> getOwn(Long requestorId);

    List<ItemRequestDto> getOtherUsersRequests(Long userId);

    ItemRequestDto getById(Long userId, Long requestId);
}
