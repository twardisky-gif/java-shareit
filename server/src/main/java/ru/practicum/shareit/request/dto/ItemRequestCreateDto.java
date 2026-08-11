package ru.practicum.shareit.request.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Данные для создания запроса вещи.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemRequestCreateDto {

    @NotBlank(message = "описание запроса не может быть пустым")
    @Size(max = 1000, message = "описание запроса не может быть длиннее 1000 символов")
    private String description;
}
