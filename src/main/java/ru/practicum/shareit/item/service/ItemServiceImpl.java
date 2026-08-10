package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.CommentCreateDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemBookingsDto;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    @Override
    @Transactional
    public ItemDto create(Long ownerId, ItemCreateDto itemCreateDto) {
        User owner = userRepository.requireById(ownerId);
        Item saved = itemRepository.save(ItemMapper.toItem(itemCreateDto, owner));
        return ItemMapper.toItemDto(saved);
    }

    @Override
    @Transactional
    public ItemDto update(Long ownerId, Long itemId, ItemUpdateDto itemUpdateDto) {
        userRepository.requireById(ownerId);
        Item stored = itemRepository.requireById(itemId);
        if (!stored.getOwner().getId().equals(ownerId)) {
            throw new NotFoundException("Вещь с id " + itemId + " не принадлежит пользователю " + ownerId);
        }
        if (itemUpdateDto.getName() != null) {
            stored.setName(itemUpdateDto.getName());
        }
        if (itemUpdateDto.getDescription() != null) {
            stored.setDescription(itemUpdateDto.getDescription());
        }
        if (itemUpdateDto.getAvailable() != null) {
            stored.setAvailable(itemUpdateDto.getAvailable());
        }
        return ItemMapper.toItemDto(itemRepository.save(stored));
    }

    @Override
    public ItemBookingsDto getById(Long userId, Long itemId) {
        Item item = itemRepository.requireById(itemId);
        List<CommentDto> comments = commentRepository.findByItemIdOrderByCreatedDesc(itemId).stream()
                .map(CommentMapper::toCommentDto)
                .toList();
        if (!item.getOwner().getId().equals(userId)) {
            return ItemMapper.toItemBookingsDto(item, null, null, comments);
        }
        List<Booking> bookings = bookingRepository.findByItemIdAndStatusOrderByStart(itemId, BookingStatus.APPROVED);
        LocalDateTime now = LocalDateTime.now();
        return ItemMapper.toItemBookingsDto(item, lastBooking(bookings, now), nextBooking(bookings, now), comments);
    }

    @Override
    public List<ItemBookingsDto> getByOwnerId(Long ownerId) {
        userRepository.requireById(ownerId);
        List<Item> items = itemRepository.findByOwnerIdOrderById(ownerId);
        if (items.isEmpty()) {
            return List.of();
        }
        List<Long> itemIds = items.stream().map(Item::getId).toList();
        Map<Long, List<Booking>> bookingsByItemId =
                bookingRepository.findByItemIdInAndStatusOrderByStart(itemIds, BookingStatus.APPROVED).stream()
                        .collect(Collectors.groupingBy(booking -> booking.getItem().getId()));
        Map<Long, List<CommentDto>> commentsByItemId =
                commentRepository.findByItemIdInOrderByCreatedDesc(itemIds).stream()
                        .collect(Collectors.groupingBy(comment -> comment.getItem().getId(),
                                Collectors.mapping(CommentMapper::toCommentDto, Collectors.toList())));
        LocalDateTime now = LocalDateTime.now();
        return items.stream()
                .map(item -> {
                    List<Booking> bookings = bookingsByItemId.getOrDefault(item.getId(), List.of());
                    return ItemMapper.toItemBookingsDto(item,
                            lastBooking(bookings, now),
                            nextBooking(bookings, now),
                            commentsByItemId.getOrDefault(item.getId(), List.of()));
                })
                .toList();
    }

    @Override
    public List<ItemDto> search(String text) {
        if (text == null || text.isBlank()) {
            return List.of();
        }
        return itemRepository.searchAvailableByText(escapeLikePattern(text)).stream()
                .map(ItemMapper::toItemDto)
                .toList();
    }

    private String escapeLikePattern(String text) {
        return text.replace("\\", "\\\\")
                .replace("%", "\\%")
                .replace("_", "\\_");
    }

    @Override
    @Transactional
    public CommentDto addComment(Long userId, Long itemId, CommentCreateDto commentCreateDto) {
        User author = userRepository.requireById(userId);
        Item item = itemRepository.requireById(itemId);
        LocalDateTime now = LocalDateTime.now();
        if (!bookingRepository.existsByBookerIdAndItemIdAndStatusAndEndBefore(
                userId, itemId, BookingStatus.APPROVED, now)) {
            throw new ValidationException("Пользователь " + userId + " не арендовал вещь " + itemId);
        }
        Comment saved = commentRepository.save(CommentMapper.toComment(commentCreateDto, item, author, now));
        return CommentMapper.toCommentDto(saved);
    }

    private BookingShortDto lastBooking(List<Booking> bookings, LocalDateTime now) {
        return bookings.stream()
                .filter(booking -> !booking.getStart().isAfter(now))
                .max(Comparator.comparing(Booking::getStart))
                .map(BookingMapper::toBookingShortDto)
                .orElse(null);
    }

    private BookingShortDto nextBooking(List<Booking> bookings, LocalDateTime now) {
        return bookings.stream()
                .filter(booking -> booking.getStart().isAfter(now))
                .min(Comparator.comparing(Booking::getStart))
                .map(BookingMapper::toBookingShortDto)
                .orElse(null);
    }
}
