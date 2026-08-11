package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.common.RequestHeaders;
import ru.practicum.shareit.item.dto.CommentCreateDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemBookingsDto;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

/**
 * REST-контроллер основного сервиса для работы с вещами.
 */
@Slf4j
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemControllerImpl implements ItemController {

    private final ItemService itemService;

    @Override
    @PostMapping
    public ItemDto create(@RequestHeader(RequestHeaders.USER_ID) Long ownerId,
                          @Valid @RequestBody ItemCreateDto itemCreateDto) {
        log.info("Добавление вещи {} владельцем {}", itemCreateDto.getName(), ownerId);
        return itemService.create(ownerId, itemCreateDto);
    }

    @Override
    @PatchMapping("/{itemId}")
    public ItemDto update(@RequestHeader(RequestHeaders.USER_ID) Long ownerId,
                          @PathVariable Long itemId,
                          @Valid @RequestBody ItemUpdateDto itemUpdateDto) {
        log.info("Обновление вещи {} владельцем {}", itemId, ownerId);
        return itemService.update(ownerId, itemId, itemUpdateDto);
    }

    @Override
    @GetMapping("/{itemId}")
    public ItemBookingsDto getById(@RequestHeader(RequestHeaders.USER_ID) Long userId,
                                   @PathVariable Long itemId) {
        return itemService.getById(userId, itemId);
    }

    @Override
    @GetMapping
    public List<ItemBookingsDto> getByOwnerId(@RequestHeader(RequestHeaders.USER_ID) Long ownerId) {
        return itemService.getByOwnerId(ownerId);
    }

    @Override
    @GetMapping("/search")
    public List<ItemDto> search(@RequestParam String text) {
        return itemService.search(text);
    }

    @Override
    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@RequestHeader(RequestHeaders.USER_ID) Long userId,
                                 @PathVariable Long itemId,
                                 @Valid @RequestBody CommentCreateDto commentCreateDto) {
        log.info("Добавление отзыва о вещи {} пользователем {}", itemId, userId);
        return itemService.addComment(userId, itemId, commentCreateDto);
    }
}
