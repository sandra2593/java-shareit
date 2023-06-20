package ru.practicum.shareit.booking.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
public class BookingTimeIntervalDto implements Comparable<BookingTimeIntervalDto> {
    int id;
    LocalDateTime start;
    LocalDateTime end;
    int bookerId;

    @Override
    public int compareTo(BookingTimeIntervalDto o) {
        return start.compareTo(o.getStart());
    }
}
