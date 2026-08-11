package ru.practicum.shareit.request;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.common.RequestHeaders;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;

/**
 * REST-контроллер gateway для работы с запросами вещей.
 */
@RestController
@RequestMapping("/requests")
@RequiredArgsConstructor
@Validated
public class ItemRequestController {

    private final ItemRequestClient itemRequestClient;

    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader(RequestHeaders.USER_ID) Long requestorId,
                                         @Valid @RequestBody ItemRequestCreateDto createDto) {
        return itemRequestClient.create(requestorId, createDto);
    }

    @GetMapping
    public ResponseEntity<Object> getOwn(@RequestHeader(RequestHeaders.USER_ID) Long requestorId) {
        return itemRequestClient.getOwn(requestorId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getOtherUsersRequests(@RequestHeader(RequestHeaders.USER_ID) Long userId) {
        return itemRequestClient.getOtherUsersRequests(userId);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getById(@RequestHeader(RequestHeaders.USER_ID) Long userId,
                                          @PathVariable Long requestId) {
        return itemRequestClient.getById(userId, requestId);
    }
}
