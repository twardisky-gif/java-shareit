package ru.practicum.shareit.booking.dto;

import java.util.Arrays;
import java.util.Optional;

/**
 * Фильтр состояния бронирований.
 */
public enum BookingState {

    ALL,
    CURRENT,
    PAST,
    FUTURE,
    WAITING,
    REJECTED;

    public static Optional<BookingState> from(String value) {
        return Arrays.stream(values())
                .filter(state -> state.name().equalsIgnoreCase(value))
                .findFirst();
    }
}
