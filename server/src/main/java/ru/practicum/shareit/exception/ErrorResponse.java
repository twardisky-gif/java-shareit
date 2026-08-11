package ru.practicum.shareit.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Описание ошибки, возвращаемой клиенту.
 */
@Getter
@RequiredArgsConstructor
public class ErrorResponse {

    private final String error;
}
