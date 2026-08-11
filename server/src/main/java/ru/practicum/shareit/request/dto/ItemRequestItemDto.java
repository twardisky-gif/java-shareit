package ru.practicum.shareit.request.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Краткое представление вещи, добавленной в ответ на запрос.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemRequestItemDto {

    private Long id;
    private String name;
    private Long ownerId;
}
