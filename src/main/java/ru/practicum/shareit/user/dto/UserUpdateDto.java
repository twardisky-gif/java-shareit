package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.validation.NullOrNotBlank;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateDto {

    @NullOrNotBlank(message = "имя не может быть пустым")
    private String name;

    @NullOrNotBlank(message = "электронная почта не может быть пустой")
    @Email(message = "некорректный формат электронной почты")
    private String email;
}
