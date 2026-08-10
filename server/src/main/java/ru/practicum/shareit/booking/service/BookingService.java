package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingState;

import java.util.List;

/**
 * Операции над бронированиями вещей.
 */
public interface BookingService {

    /**
     * Создаёт запрос на бронирование доступной вещи в статусе ожидания подтверждения.
     *
     * @param bookerId идентификатор пользователя, который бронирует вещь
     * @param bookingCreateDto вещь и даты бронирования
     * @return созданное бронирование в статусе WAITING
     */
    BookingDto create(Long bookerId, BookingCreateDto bookingCreateDto);

    /**
     * Подтверждает или отклоняет бронирование. Доступно только владельцу вещи.
     *
     * @param ownerId идентификатор владельца вещи
     * @param bookingId идентификатор бронирования
     * @param approved true — подтвердить, false — отклонить
     * @return бронирование в статусе APPROVED или REJECTED
     */
    BookingDto approve(Long ownerId, Long bookingId, boolean approved);

    /**
     * Возвращает бронирование. Доступно автору бронирования и владельцу вещи.
     *
     * @param userId идентификатор запрашивающего пользователя
     * @param bookingId идентификатор бронирования
     * @return найденное бронирование
     */
    BookingDto getById(Long userId, Long bookingId);

    /**
     * Возвращает бронирования пользователя, отсортированные от новых к старым.
     *
     * @param bookerId идентификатор пользователя
     * @param state фильтр по состоянию бронирования
     * @return список бронирований пользователя
     */
    List<BookingDto> getByBooker(Long bookerId, BookingState state);

    /**
     * Возвращает бронирования всех вещей владельца, отсортированные от новых к старым.
     *
     * @param ownerId идентификатор владельца вещей
     * @param state фильтр по состоянию бронирования
     * @return список бронирований вещей владельца
     */
    List<BookingDto> getByOwner(Long ownerId, BookingState state);
}
