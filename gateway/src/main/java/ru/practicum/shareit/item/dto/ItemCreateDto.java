package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemCreateDto {

    @NotBlank(message = "название не может быть пустым")
    @Size(max = 255, message = "название не может быть длиннее 255 символов")
    private String name;

    @NotBlank(message = "описание не может быть пустым")
    @Size(max = 1000, message = "описание не может быть длиннее 1000 символов")
    private String description;

    @NotNull(message = "статус доступности обязателен")
    private Boolean available;

    private Long requestId;

    public ItemCreateDto(String name, String description, Boolean available) {
        this.name = name;
        this.description = description;
        this.available = available;
        this.requestId = null;
    }
}
