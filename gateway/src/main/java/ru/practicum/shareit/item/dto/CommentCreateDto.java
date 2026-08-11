package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Данные для создания комментария.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentCreateDto {

    @NotBlank(message = "текст комментария не может быть пустым")
    @Size(max = 2000, message = "текст комментария не может быть длиннее 2000 символов")
    private String text;
}
