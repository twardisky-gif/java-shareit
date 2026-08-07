package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public BookingDto create(Long bookerId, BookingCreateDto bookingCreateDto) {
        User booker = userRepository.requireById(bookerId);
        Item item = itemRepository.requireById(bookingCreateDto.getItemId());
        if (!bookingCreateDto.getEnd().isAfter(bookingCreateDto.getStart())) {
            throw new ValidationException("Дата окончания бронирования должна быть позже даты начала");
        }
        if (!Boolean.TRUE.equals(item.getAvailable())) {
            throw new ValidationException("Вещь с id " + item.getId() + " недоступна для бронирования");
        }
        if (item.getOwner().getId().equals(bookerId)) {
            throw new NotFoundException("Владелец не может забронировать собственную вещь");
        }
        requirePeriodFree(item.getId(), bookingCreateDto.getStart(), bookingCreateDto.getEnd());
        Booking saved = bookingRepository.save(BookingMapper.toBooking(bookingCreateDto, item, booker));
        return BookingMapper.toBookingDto(saved);
    }

    @Override
    @Transactional
    public BookingDto approve(Long ownerId, Long bookingId, boolean approved) {
        Booking booking = bookingRepository.requireById(bookingId);
        if (!booking.getItem().getOwner().getId().equals(ownerId)) {
            throw new ForbiddenException("Пользователь " + ownerId + " не является владельцем вещи");
        }
        if (booking.getStatus() != BookingStatus.WAITING) {
            throw new ValidationException("Бронирование " + bookingId + " уже обработано");
        }
        if (approved) {
            requirePeriodFree(booking.getItem().getId(), booking.getStart(), booking.getEnd());
        }
        booking.setStatus(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);
        return BookingMapper.toBookingDto(bookingRepository.save(booking));
    }

    @Override
    public BookingDto getById(Long userId, Long bookingId) {
        Booking booking = bookingRepository.requireById(bookingId);
        boolean isBooker = booking.getBooker().getId().equals(userId);
        boolean isOwner = booking.getItem().getOwner().getId().equals(userId);
        if (!isBooker && !isOwner) {
            throw new NotFoundException("Бронирование " + bookingId + " недоступно пользователю " + userId);
        }
        return BookingMapper.toBookingDto(booking);
    }

    @Override
    public List<BookingDto> getByBooker(Long bookerId, BookingState state) {
        userRepository.requireById(bookerId);
        LocalDateTime now = LocalDateTime.now();
        List<Booking> bookings = switch (state) {
            case CURRENT -> bookingRepository
                    .findByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(bookerId, now, now);
            case PAST -> bookingRepository.findByBookerIdAndEndBeforeOrderByStartDesc(bookerId, now);
            case FUTURE -> bookingRepository.findByBookerIdAndStartAfterOrderByStartDesc(bookerId, now);
            case WAITING -> bookingRepository
                    .findByBookerIdAndStatusOrderByStartDesc(bookerId, BookingStatus.WAITING);
            case REJECTED -> bookingRepository
                    .findByBookerIdAndStatusOrderByStartDesc(bookerId, BookingStatus.REJECTED);
            case ALL -> bookingRepository.findByBookerIdOrderByStartDesc(bookerId);
        };
        return toDtoList(bookings);
    }

    @Override
    public List<BookingDto> getByOwner(Long ownerId, BookingState state) {
        userRepository.requireById(ownerId);
        LocalDateTime now = LocalDateTime.now();
        List<Booking> bookings = switch (state) {
            case CURRENT -> bookingRepository
                    .findByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(ownerId, now, now);
            case PAST -> bookingRepository.findByItemOwnerIdAndEndBeforeOrderByStartDesc(ownerId, now);
            case FUTURE -> bookingRepository.findByItemOwnerIdAndStartAfterOrderByStartDesc(ownerId, now);
            case WAITING -> bookingRepository
                    .findByItemOwnerIdAndStatusOrderByStartDesc(ownerId, BookingStatus.WAITING);
            case REJECTED -> bookingRepository
                    .findByItemOwnerIdAndStatusOrderByStartDesc(ownerId, BookingStatus.REJECTED);
            case ALL -> bookingRepository.findByItemOwnerIdOrderByStartDesc(ownerId);
        };
        return toDtoList(bookings);
    }

    private void requirePeriodFree(Long itemId, LocalDateTime start, LocalDateTime end) {
        if (bookingRepository.existsByItemIdAndStatusAndStartLessThanAndEndGreaterThan(
                itemId, BookingStatus.APPROVED, end, start)) {
            throw new ValidationException("Вещь с id " + itemId + " уже забронирована на эти даты");
        }
    }

    private List<BookingDto> toDtoList(List<Booking> bookings) {
        return bookings.stream()
                .map(BookingMapper::toBookingDto)
                .toList();
    }
}
