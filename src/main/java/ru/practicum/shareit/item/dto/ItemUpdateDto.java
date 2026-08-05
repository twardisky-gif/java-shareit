package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemUpdateDto {

    @Pattern(regexp = ".*\\S.*", message = "название не может быть пустым")
    private String name;

    @Pattern(regexp = ".*\\S.*", message = "описание не может быть пустым")
    private String description;

    private Boolean available;
}
