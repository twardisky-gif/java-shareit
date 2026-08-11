package ru.practicum.shareit.item.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.common.DateTimePatterns;

import java.time.LocalDateTime;

/**
 * Данные комментария к вещи.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentDto {

    private Long id;
    private String text;
    private String authorName;

    @JsonFormat(pattern = DateTimePatterns.ISO_LOCAL_DATE_TIME_SECONDS)
    private LocalDateTime created;
}
