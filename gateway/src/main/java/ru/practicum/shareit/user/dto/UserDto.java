package ru.practicum.shareit.user.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
    int id;
    @NotNull(message = "Имя не должно быть пустым")
    String name;
    @NotNull(message = "Email не должен быть пустым")
    @Email(message = "Некорректный формат почты")
    String email;
}
