package ru.practicum.shareit.booking.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Данные для создания бронирования.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingCreateDto {

    @NotNull(message = "идентификатор вещи обязателен")
    private Long itemId;

    @NotNull(message = "дата начала бронирования обязательна")
    @FutureOrPresent(message = "дата начала бронирования не может быть в прошлом")
    private LocalDateTime start;

    @NotNull(message = "дата окончания бронирования обязательна")
    @Future(message = "дата окончания бронирования должна быть в будущем")
    private LocalDateTime end;
}
