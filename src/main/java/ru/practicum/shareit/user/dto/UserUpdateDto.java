package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateDto {

    @Pattern(regexp = ".*\\S.*", message = "имя не может быть пустым")
    private String name;

    @Pattern(regexp = ".*\\S.*", message = "электронная почта не может быть пустой")
    @Email(message = "некорректный формат электронной почты")
    private String email;
}
