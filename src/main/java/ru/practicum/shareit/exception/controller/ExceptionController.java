package ru.practicum.shareit.exception.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.exception.*;

import java.util.Map;

@RestControllerAdvice
public class ExceptionController {

    @ExceptionHandler({ValidationException.class, NotOwnerException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleValidationExceptions(ValidationException ex) {
        return Map.of("message", ex.getMessage());
    }

    @ExceptionHandler({UnavailableItemException.class, SameApproveStatusException.class, CannotCommentException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleBadRequestExceptions(RuntimeException ex) {
        return Map.of("message", ex.getMessage());
    }

    @ExceptionHandler({NotFoundException.class, NoAccessToBookException.class, CannotApproveException.class, BookerIsOwnerException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleNotFoundExceptions(RuntimeException ex) {
        return Map.of("message", ex.getMessage());
    }

    @ExceptionHandler({DuplicateEmailException.class})
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String, String> handleDuplicateEmailExceptions(RuntimeException ex) {
        return Map.of("message", ex.getMessage());
    }

    @ExceptionHandler(WrongBookingStatusException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleWrongStatusException(RuntimeException ex) {
        return Map.of("error", ex.getCause().getCause().getMessage());
    }
}