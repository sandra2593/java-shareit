package ru.practicum.shareit.exception;

public class UnavailableItemException extends RuntimeException {

    public UnavailableItemException(final String message) {
        super(message);
    }

}
