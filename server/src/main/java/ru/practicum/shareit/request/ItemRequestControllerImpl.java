package ru.practicum.shareit.request;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.common.RequestHeaders;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/requests")
@RequiredArgsConstructor
public class ItemRequestControllerImpl implements ItemRequestController {

    private final ItemRequestService itemRequestService;

    @Override
    @PostMapping
    public ItemRequestDto create(@RequestHeader(RequestHeaders.USER_ID) Long requestorId,
                                 @Valid @RequestBody ItemRequestCreateDto createDto) {
        log.info("Создание запроса вещи пользователем {}", requestorId);
        return itemRequestService.create(requestorId, createDto);
    }

    @Override
    @GetMapping
    public List<ItemRequestDto> getOwn(@RequestHeader(RequestHeaders.USER_ID) Long requestorId) {
        return itemRequestService.getOwn(requestorId);
    }

    @Override
    @GetMapping("/all")
    public List<ItemRequestDto> getOtherUsersRequests(@RequestHeader(RequestHeaders.USER_ID) Long userId) {
        return itemRequestService.getOtherUsersRequests(userId);
    }

    @Override
    @GetMapping("/{requestId}")
    public ItemRequestDto getById(@RequestHeader(RequestHeaders.USER_ID) Long userId,
                                  @PathVariable Long requestId) {
        return itemRequestService.getById(userId, requestId);
    }
}
