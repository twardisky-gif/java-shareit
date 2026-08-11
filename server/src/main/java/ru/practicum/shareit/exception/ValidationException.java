package ru.practicum.shareit.exception;

/**
 * Исключение при нарушении бизнес-правил валидации.
 */
public class ValidationException extends RuntimeException {

    public ValidationException(String message) {
        super(message);
    }
}
