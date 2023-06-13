package ru.practicum.shareit.item.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.booking.dto.BookingTimeIntervalDto;
import ru.practicum.shareit.comment.dto.CommentDto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Collection;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
public class ItemDto {
    int id;
    @NotBlank(message = "Название не должно быть пустым")
    String name;
    @NotNull(message = "Описание не должно быть пустым")
    String description;
    @NotNull(message = "Доступность должена быть указана")
    Boolean available;
    BookingTimeIntervalDto lastBooking;
    BookingTimeIntervalDto nextBooking;
    Collection<CommentDto> comments;
    Integer requestId;
}
