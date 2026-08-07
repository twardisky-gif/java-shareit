package ru.practicum.shareit.booking;

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
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.common.RequestHeaders;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
public class BookingControllerImpl implements BookingController {

    private final BookingService bookingService;

    @Override
    @PostMapping
    public BookingDto create(@RequestHeader(RequestHeaders.USER_ID) Long bookerId,
                             @Valid @RequestBody BookingCreateDto bookingCreateDto) {
        log.info("Бронирование вещи {} пользователем {}", bookingCreateDto.getItemId(), bookerId);
        return bookingService.create(bookerId, bookingCreateDto);
    }

    @Override
    @PatchMapping("/{bookingId}")
    public BookingDto approve(@RequestHeader(RequestHeaders.USER_ID) Long ownerId,
                              @PathVariable Long bookingId,
                              @RequestParam boolean approved) {
        log.info("Изменение статуса бронирования {} владельцем {} на approved={}", bookingId, ownerId, approved);
        return bookingService.approve(ownerId, bookingId, approved);
    }

    @Override
    @GetMapping("/{bookingId}")
    public BookingDto getById(@RequestHeader(RequestHeaders.USER_ID) Long userId,
                              @PathVariable Long bookingId) {
        return bookingService.getById(userId, bookingId);
    }

    @Override
    @GetMapping
    public List<BookingDto> getByBooker(@RequestHeader(RequestHeaders.USER_ID) Long bookerId,
                                        @RequestParam(defaultValue = "ALL") BookingState state) {
        return bookingService.getByBooker(bookerId, state);
    }

    @Override
    @GetMapping("/owner")
    public List<BookingDto> getByOwner(@RequestHeader(RequestHeaders.USER_ID) Long ownerId,
                                       @RequestParam(defaultValue = "ALL") BookingState state) {
        return bookingService.getByOwner(ownerId, state);
    }
}
