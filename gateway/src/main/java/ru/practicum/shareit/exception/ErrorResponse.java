package ru.practicum.shareit.exception;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Описание ошибки, возвращаемой клиенту.
 */
@Data
@AllArgsConstructor
public class ErrorResponse {

    private String error;
}
