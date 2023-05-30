package ru.practicum.shareit.convert;

import org.springframework.core.convert.converter.Converter;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.exception.WrongBookingStatusException;

public class StringToEnumBookingStateConverter implements Converter<String, BookingState> {
    @Override
    public BookingState convert(String source) {
        try {
            return BookingState.valueOf(source.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new WrongBookingStatusException(source);
        }
    }
}
