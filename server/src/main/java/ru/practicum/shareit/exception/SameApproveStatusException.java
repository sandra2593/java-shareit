package ru.practicum.shareit.exception;

public class SameApproveStatusException extends RuntimeException {

    public SameApproveStatusException(final String message) {
        super(message);
    }

}
