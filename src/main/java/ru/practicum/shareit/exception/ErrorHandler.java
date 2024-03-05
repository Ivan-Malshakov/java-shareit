package ru.practicum.shareit.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.exception.exceptions.ConflictEmailException;
import ru.practicum.shareit.exception.exceptions.DataNotFoundException;
import ru.practicum.shareit.exception.exceptions.ForbiddenUpdateException;
import ru.practicum.shareit.exception.exceptions.ValidationException;

@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public int handleDataNotFoundException(final DataNotFoundException e) {
        return new ErrorResponse("Data error", e.getMessage(), 404).getStatus();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public int handleConflictEmailException(final ConflictEmailException e) {
        return new ErrorResponse("Email error", e.getMessage(), 409).getStatus();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public int handleForbiddenUpdateException(final ForbiddenUpdateException e) {
        return new ErrorResponse("User error", e.getMessage(), 403).getStatus();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public int handleValidationException(final ValidationException e) {
        return new ErrorResponse("Validation error", e.getMessage(), 400).getStatus();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public int handleMethodArgumentNotValidException(final MethodArgumentNotValidException e) {
        return new ErrorResponse("Validation error", e.getMessage(), 400).getStatus();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public int handleRuntimeException(final Exception e) {
        return new ErrorResponse("Error", e.getMessage(), 500).getStatus();
    }

}

