package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.client.BookingClient;
import ru.practicum.shareit.booking.dto.BookingDtoShort;
import ru.practicum.shareit.booking.dto.BookingState;

import javax.validation.Valid;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Validated
public class BookingController {

    private final BookingClient bookingClient;
    private static final String HEADER_PARAM = "X-Sharer-User-Id";

    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader(value = HEADER_PARAM) int userId, @Valid @RequestBody BookingDtoShort newBookingDto) {
        return bookingClient.create(userId, newBookingDto);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> approve(@PathVariable int bookingId, @RequestHeader(HEADER_PARAM) int userId, @RequestParam boolean approved) {
        return bookingClient.approve(bookingId, userId, approved);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBookingById(@PathVariable int bookingId, @RequestHeader(HEADER_PARAM) int userId) {
        return bookingClient.getBookingById(bookingId, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getUserBookings(@RequestHeader(HEADER_PARAM) int userId, @RequestParam(defaultValue = "ALL") BookingState state, @RequestParam(defaultValue = "0") int from, @RequestParam(defaultValue = "2000") int size) {
        return bookingClient.getUserBookings(userId, state, from, size);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getOwnedItemsBookings(@RequestHeader(HEADER_PARAM) int ownerId, @RequestParam(defaultValue = "ALL") BookingState state, @RequestParam(defaultValue = "0") int from, @RequestParam(defaultValue = "2000") int size) {
        return bookingClient.getOwnedItemsBookings(ownerId, state, from, size);
    }
}