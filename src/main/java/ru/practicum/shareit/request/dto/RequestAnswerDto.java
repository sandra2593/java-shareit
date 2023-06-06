package ru.practicum.shareit.request.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.item.dto.ItemRequestDto;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.Collection;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RequestAnswerDto {
    Collection<ItemRequestDto> items;
    int id;
    @NotBlank(message = "Описание запроса не пустое")
    String description;
    LocalDateTime created;
}
