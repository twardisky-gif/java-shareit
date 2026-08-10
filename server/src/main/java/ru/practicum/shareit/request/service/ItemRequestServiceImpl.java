package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestItemDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository itemRequestRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public ItemRequestDto create(Long requestorId, ItemRequestCreateDto createDto) {
        User requestor = userRepository.requireById(requestorId);
        ItemRequest saved = itemRequestRepository.save(
                ItemRequestMapper.toItemRequest(createDto, requestor, LocalDateTime.now()));
        return ItemRequestMapper.toItemRequestDto(saved, List.of());
    }

    @Override
    public List<ItemRequestDto> getOwn(Long requestorId) {
        userRepository.requireById(requestorId);
        List<ItemRequest> requests = itemRequestRepository.findByRequestorIdOrderByCreatedDesc(requestorId);
        return mapRequests(requests);
    }

    @Override
    public List<ItemRequestDto> getOtherUsersRequests(Long userId) {
        userRepository.requireById(userId);
        List<ItemRequest> requests = itemRequestRepository.findByRequestorIdNotOrderByCreatedDesc(userId);
        return mapRequests(requests);
    }

    @Override
    public ItemRequestDto getById(Long userId, Long requestId) {
        userRepository.requireById(userId);
        ItemRequest request = itemRequestRepository.requireById(requestId);
        List<ItemRequestItemDto> items = itemRepository.findByRequestIdInOrderById(List.of(requestId)).stream()
                .map(ItemRequestMapper::toItemRequestItemDto)
                .toList();
        return ItemRequestMapper.toItemRequestDto(request, items);
    }

    private List<ItemRequestDto> mapRequests(List<ItemRequest> requests) {
        if (requests.isEmpty()) {
            return List.of();
        }
        List<Long> requestIds = requests.stream()
                .map(ItemRequest::getId)
                .toList();
        Map<Long, List<ItemRequestItemDto>> itemsByRequestId = itemRepository.findByRequestIdInOrderById(requestIds).stream()
                .collect(Collectors.groupingBy(item -> item.getRequest().getId(),
                        Collectors.mapping(ItemRequestMapper::toItemRequestItemDto, Collectors.toList())));
        return requests.stream()
                .map(request -> ItemRequestMapper.toItemRequestDto(
                        request, itemsByRequestId.getOrDefault(request.getId(), List.of())))
                .toList();
    }
}
