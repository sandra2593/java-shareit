package ru.practicum.shareit.exception;

public class WrongBookingStatusException extends RuntimeException {
    public WrongBookingStatusException(String status) {
        super(String.format("Unknown state: %s", status));
    }
}
