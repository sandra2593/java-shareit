package ru.practicum.shareit.request.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.user.dto.UserDtoShort;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
public class RequestDto {
    int id;
    @NotBlank(message = "Описание запроса не пустое")
    String description;
    UserDtoShort requestor;
    LocalDateTime created;
}
