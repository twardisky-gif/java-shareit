package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.validation.NullOrNotBlank;

/**
 * Данные для обновления вещи.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemUpdateDto {

    @NullOrNotBlank(message = "название не может быть пустым")
    @Size(max = 255, message = "название не может быть длиннее 255 символов")
    private String name;

    @NullOrNotBlank(message = "описание не может быть пустым")
    @Size(max = 1000, message = "описание не может быть длиннее 1000 символов")
    private String description;

    private Boolean available;
}
