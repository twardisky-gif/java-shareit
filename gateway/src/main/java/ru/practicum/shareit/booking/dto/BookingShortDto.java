package ru.practicum.shareit.booking.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.common.DateTimePatterns;

import java.time.LocalDateTime;

/**
 * Краткие данные бронирования.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingShortDto {

    private Long id;
    private Long bookerId;

    @JsonFormat(pattern = DateTimePatterns.ISO_LOCAL_DATE_TIME_SECONDS)
    private LocalDateTime start;

    @JsonFormat(pattern = DateTimePatterns.ISO_LOCAL_DATE_TIME_SECONDS)
    private LocalDateTime end;
}
