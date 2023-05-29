package ru.practicum.shareit.booking;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoShort;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.Valid;
import java.util.Collection;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = "/bookings")
public class BookingController {

    private final BookingService bookingService;

    @Autowired
    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    BookingDto createBooking(@RequestHeader(value = "X-Sharer-User-Id") int userId, @Valid @RequestBody BookingDtoShort newBookingDto
    ) {
        return BookingMapper.toBookingDto(bookingService.createBooking(userId, newBookingDto));
    }

    @PatchMapping("/{bookingId}")
    BookingDto approveBooking(@PathVariable int bookingId, @RequestHeader("X-Sharer-User-Id") int userId, @RequestParam boolean approved) {
        return BookingMapper.toBookingDto(bookingService.approveBooking(bookingId, userId, approved));
    }

    @GetMapping("/{bookingId}")
    BookingDto getBookingById(@PathVariable int bookingId, @RequestHeader("X-Sharer-User-Id") int userId) {
        return BookingMapper.toBookingDto(bookingService.getBookingById(bookingId, userId));
    }

    @GetMapping
    Collection<BookingDto> getUserBookings(@RequestHeader("X-Sharer-User-Id") int userId, @RequestParam(defaultValue = "ALL") BookingState state) {
        return bookingService.getUserBookings(userId, state).stream().map(BookingMapper::toBookingDto).collect(Collectors.toList());
    }

    @GetMapping("/owner")
    Collection<BookingDto> getOwnedItemsBookings(@RequestHeader("X-Sharer-User-Id") int ownerId, @RequestParam(defaultValue = "ALL") BookingState state) {
        return bookingService.getOwnedItemsBookings(ownerId, state).stream().map(BookingMapper::toBookingDto).collect(Collectors.toList());
    }
}