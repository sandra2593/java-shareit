package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDtoShort;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;

import java.util.Collection;

public interface BookingServiceInterface {
    Booking create(int userId, BookingDtoShort newBooking);

    Booking approve(int bookingId, int userId, boolean isApproved);

    Booking getBookingById(int bookingId, int userId);

    Collection<Booking> getUserBookings(int userId, BookingState state, int from, int size);

    Collection<Booking> getOwnedItemsBookings(int ownerId, BookingState state, int from, int size);
}
