package ru.practicum.shareit.exception;

/**
 * Исключение при отсутствии запрошенного ресурса.
 */
public class NotFoundException extends RuntimeException {

    public NotFoundException(String message) {
        super(message);
    }
}
