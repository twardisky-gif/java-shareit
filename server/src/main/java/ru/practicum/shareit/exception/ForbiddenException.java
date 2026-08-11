package ru.practicum.shareit.exception;

/**
 * Исключение при попытке выполнить запрещённую операцию.
 */
public class ForbiddenException extends RuntimeException {

    public ForbiddenException(String message) {
        super(message);
    }
}
