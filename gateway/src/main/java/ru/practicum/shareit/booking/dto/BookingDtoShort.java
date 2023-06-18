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
    @NotNull(message = "Идентификатор вещи не пустой")
    int itemId;
    @NotNull(message = "Дата начала брони не пустая")
    LocalDateTime start;
    @NotNull(message = "Дата окончания брони не пустая")
    LocalDateTime end;
}
