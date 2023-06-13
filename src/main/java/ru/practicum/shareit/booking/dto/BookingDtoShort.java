package ru.practicum.shareit.booking.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
public class BookingDtoShort {
    @NotNull(message = "Идентификатор вещи для брони не может быть пустым.")
    int itemId;
    @NotNull(message = "Дата начала брони не может быть пустой.")
    LocalDateTime start;
    @NotNull(message = "Дата окончания брони не может быть пустой.")
    LocalDateTime end;
}
