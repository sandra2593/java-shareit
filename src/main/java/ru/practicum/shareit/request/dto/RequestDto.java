package ru.practicum.shareit.request.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.user.dto.UserDtoShort;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RequestDto {
    int id;
    @NotBlank(message = "Описание запроса не пустое")
    String description;
    UserDtoShort requestor;
    LocalDateTime created;
}
