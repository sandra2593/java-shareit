package ru.practicum.shareit.booking.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.dto.ItemDtoShort;
import ru.practicum.shareit.user.dto.UserDtoShort;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BookingDto {
    int id;
    @NotNull(message = "Дата начала брони не пустая")
    LocalDateTime start;
    @NotNull(message = "Дата окончания брони не пустая")
    LocalDateTime end;
    ItemDtoShort item;
    UserDtoShort booker;
    BookingStatus status;
}
