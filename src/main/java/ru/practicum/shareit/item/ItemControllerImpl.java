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
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemControllerImpl implements ItemController {

    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    private final ItemService itemService;

    @Override
    @PostMapping
    public ItemDto create(@RequestHeader(USER_ID_HEADER) Long ownerId,
                          @Valid @RequestBody ItemCreateDto itemCreateDto) {
        log.info("Добавление вещи {} владельцем {}", itemCreateDto.getName(), ownerId);
        return itemService.create(ownerId, itemCreateDto);
    }

    @Override
    @PatchMapping("/{itemId}")
    public ItemDto update(@RequestHeader(USER_ID_HEADER) Long ownerId,
                          @PathVariable Long itemId,
                          @Valid @RequestBody ItemUpdateDto itemUpdateDto) {
        log.info("Обновление вещи {} владельцем {}", itemId, ownerId);
        return itemService.update(ownerId, itemId, itemUpdateDto);
    }

    @Override
    @GetMapping("/{itemId}")
    public ItemDto getById(@PathVariable Long itemId) {
        return itemService.getById(itemId);
    }

    @Override
    @GetMapping
    public List<ItemDto> getByOwnerId(@RequestHeader(USER_ID_HEADER) Long ownerId) {
        return itemService.getByOwnerId(ownerId);
    }

    @Override
    @GetMapping("/search")
    public List<ItemDto> search(@RequestParam String text) {
        return itemService.search(text);
    }
}
