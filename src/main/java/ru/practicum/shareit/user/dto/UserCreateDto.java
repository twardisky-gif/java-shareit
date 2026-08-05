package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserCreateDto {

    @NotBlank(message = "имя не может быть пустым")
    private String name;

    @NotBlank(message = "электронная почта не может быть пустой")
    @Email(message = "некорректный формат электронной почты")
    private String email;
}
