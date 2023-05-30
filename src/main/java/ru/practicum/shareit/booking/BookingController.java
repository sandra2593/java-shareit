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
    private static final String HEADER_PARAM = "X-Sharer-User-Id";

    @Autowired
    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    BookingDto createBooking(@RequestHeader(value = HEADER_PARAM) int userId, @Valid @RequestBody BookingDtoShort newBookingDto
    ) {
        return BookingMapper.toBookingDto(bookingService.create(userId, newBookingDto));
    }

    @PatchMapping("/{bookingId}")
    BookingDto approveBooking(@PathVariable int bookingId, @RequestHeader(HEADER_PARAM) int userId, @RequestParam boolean approved) {
        return BookingMapper.toBookingDto(bookingService.approve(bookingId, userId, approved));
    }

    @GetMapping("/{bookingId}")
    BookingDto getBookingById(@PathVariable int bookingId, @RequestHeader(HEADER_PARAM) int userId) {
        return BookingMapper.toBookingDto(bookingService.getBookingById(bookingId, userId));
    }

    @GetMapping
    Collection<BookingDto> getUserBookings(@RequestHeader(HEADER_PARAM) int userId, @RequestParam(defaultValue = "ALL") BookingState state) {
        return bookingService.getUserBookings(userId, state).stream().map(BookingMapper::toBookingDto).collect(Collectors.toList());
    }

    @GetMapping("/owner")
    Collection<BookingDto> getOwnedItemsBookings(@RequestHeader(HEADER_PARAM) int ownerId, @RequestParam(defaultValue = "ALL") BookingState state) {
        return bookingService.getOwnedItemsBookings(ownerId, state).stream().map(BookingMapper::toBookingDto).collect(Collectors.toList());
    }
}