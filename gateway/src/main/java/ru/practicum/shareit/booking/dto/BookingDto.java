package ru.practicum.shareit.booking.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.item.dto.ItemDtoShort;
import ru.practicum.shareit.user.dto.UserDtoShort;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
public class BookingDto {
    int id;
    @NotNull(message = "Дата начала брони не пустая")
    LocalDateTime start;
    @NotNull(message = "Дата окончания брони не пустая")
    LocalDateTime end;
    ItemDtoShort item;
    UserDtoShort booker;
    BookingState status;
}
