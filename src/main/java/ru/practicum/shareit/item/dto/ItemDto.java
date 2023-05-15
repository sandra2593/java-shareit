package ru.practicum.shareit.item.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemDto {
    int id;
    @NotBlank(message = "Название не должно быть пустым")
    String name;
    @NotNull(message = "Описание не должно быть пустым")
    String description;
    @NotNull(message = "Доступность должена быть указана")
    Boolean available;
}
