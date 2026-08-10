package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
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
    @Size(max = 255, message = "имя не может быть длиннее 255 символов")
    private String name;

    @NotBlank(message = "электронная почта не может быть пустой")
    @Email(message = "некорректный формат электронной почты")
    @Size(max = 512, message = "электронная почта не может быть длиннее 512 символов")
    private String email;
}
