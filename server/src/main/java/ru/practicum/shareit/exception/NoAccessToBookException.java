package ru.practicum.shareit.exception;

public class NoAccessToBookException extends RuntimeException {

    public NoAccessToBookException(final String message) {
        super(message);
    }

}
