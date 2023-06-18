package ru.practicum.shareit.booking;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoShort;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingState;
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
    public BookingDto create(@RequestHeader(value = HEADER_PARAM) int userId, @Valid @RequestBody BookingDtoShort newBookingDto) {
        return BookingMapper.toBookingDto(bookingService.create(userId, newBookingDto));
    }

    @PatchMapping("/{bookingId}")
    public BookingDto approve(@PathVariable int bookingId, @RequestHeader(HEADER_PARAM) int userId, @RequestParam boolean approved) {
        return BookingMapper.toBookingDto(bookingService.approve(bookingId, userId, approved));
    }

    @GetMapping("/{bookingId}")
    public BookingDto getBookingById(@PathVariable int bookingId, @RequestHeader(HEADER_PARAM) int userId) {
        return BookingMapper.toBookingDto(bookingService.getBookingById(bookingId, userId));
    }

    @GetMapping
    public Collection<BookingDto> getUserBookings(@RequestHeader(HEADER_PARAM) int userId, @RequestParam(defaultValue = "ALL") BookingState state, @RequestParam(defaultValue = "0") int from, @RequestParam(defaultValue = "2000") int size) {
        return bookingService.getUserBookings(userId, state, from, size).stream().map(BookingMapper::toBookingDto).collect(Collectors.toList());
    }

    @GetMapping("/owner")
    public Collection<BookingDto> getOwnedItemsBookings(@RequestHeader(HEADER_PARAM) int ownerId, @RequestParam(defaultValue = "ALL") BookingState state, @RequestParam(defaultValue = "0") int from, @RequestParam(defaultValue = "2000") int size) {
        return bookingService.getOwnedItemsBookings(ownerId, state, from, size).stream().map(BookingMapper::toBookingDto).collect(Collectors.toList());
    }
}