package ru.practicum.shareit.exception;

public class NotOwnerException extends RuntimeException {

    public NotOwnerException(final String message) {
        super(message);
    }

}
