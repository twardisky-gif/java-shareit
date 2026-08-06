package ru.practicum.shareit.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Ограничение для полей частичного обновления: поле можно не передавать,
 * но переданное значение не должно быть пустым.
 * В отличие от {@link jakarta.validation.constraints.NotBlank} пропускает null.
 */
@Documented
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = NullOrNotBlankValidator.class)
public @interface NullOrNotBlank {

    String message() default "значение не может быть пустым";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
