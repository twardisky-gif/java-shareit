package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.validation.NullOrNotBlank;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemUpdateDto {

    @NullOrNotBlank(message = "название не может быть пустым")
    private String name;

    @NullOrNotBlank(message = "описание не может быть пустым")
    private String description;

    private Boolean available;
}
