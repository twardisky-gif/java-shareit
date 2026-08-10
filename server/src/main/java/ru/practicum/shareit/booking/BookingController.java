package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingState;

import java.util.List;

/**
 * REST-контракт для работы с бронированиями вещей.
 */
public interface BookingController {

    /**
     * Создаёт запрос на бронирование вещи.
     *
     * @param bookerId идентификатор пользователя из заголовка X-Sharer-User-Id
     * @param bookingCreateDto вещь и даты бронирования
     * @return созданное бронирование в статусе WAITING
     */
    BookingDto create(Long bookerId, BookingCreateDto bookingCreateDto);

    /**
     * Подтверждает или отклоняет бронирование. Доступно только владельцу вещи.
     *
     * @param ownerId идентификатор владельца вещи из заголовка X-Sharer-User-Id
     * @param bookingId идентификатор бронирования
     * @param approved true — подтвердить, false — отклонить
     * @return бронирование с новым статусом
     */
    BookingDto approve(Long ownerId, Long bookingId, boolean approved);

    /**
     * Возвращает бронирование. Доступно автору бронирования и владельцу вещи.
     *
     * @param userId идентификатор пользователя из заголовка X-Sharer-User-Id
     * @param bookingId идентификатор бронирования
     * @return найденное бронирование
     */
    BookingDto getById(Long userId, Long bookingId);

    /**
     * Возвращает бронирования текущего пользователя от новых к старым.
     *
     * @param bookerId идентификатор пользователя из заголовка X-Sharer-User-Id
     * @param state фильтр по состоянию бронирования, по умолчанию ALL
     * @return список бронирований пользователя
     */
    List<BookingDto> getByBooker(Long bookerId, BookingState state);

    /**
     * Возвращает бронирования всех вещей текущего пользователя от новых к старым.
     *
     * @param ownerId идентификатор владельца вещей из заголовка X-Sharer-User-Id
     * @param state фильтр по состоянию бронирования, по умолчанию ALL
     * @return список бронирований вещей владельца
     */
    List<BookingDto> getByOwner(Long ownerId, BookingState state);
}
