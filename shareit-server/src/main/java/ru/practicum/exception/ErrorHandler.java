package ru.practicum.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.exception.exceptions.*;

import javax.validation.ConstraintViolationException;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleDataNotFoundException(final DataNotFoundException e) {
        log.warn(e.getMessage());
        return new ErrorResponse("Data error", e.getMessage(), 404);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleForbiddenAccessException(final ForbiddenAccessException e) {
        log.warn(e.getMessage());
        return new ErrorResponse("Access error", e.getMessage(), 404);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponse handleForbiddenUpdateException(final ForbiddenUpdateException e) {
        log.warn(e.getMessage());
        return new ErrorResponse("Update error", e.getMessage(), 403);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleBookingDateException(final BookingDateException e) {
        log.warn(e.getMessage());
        return new ErrorResponse("Date error", e.getMessage(), 400);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleBookingNotFoundException(final BookingNotFoundException e) {
        log.warn(e.getMessage());
        return new ErrorResponse("Booking error", e.getMessage(), 400);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleStateNotFoundException(final StateNotFoundException e) {
        log.warn(e.getMessage());
        return new ErrorResponse(e.getMessage(), e.getMessage(), 400);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleForbiddenAccessChangeStatusException(final ForbiddenAccessChangeStatusException e) {
        log.warn(e.getMessage());
        return new ErrorResponse("Access error", e.getMessage(), 400);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleUnavailableItemException(final UnavailableItemException e) {
        log.warn(e.getMessage());
        return new ErrorResponse("Available error", e.getMessage(), 400);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleMethodArgumentNotValidException(final MethodArgumentNotValidException e) {
        log.warn(e.getMessage());
        return new ErrorResponse("Validation error", e.getMessage(), 400);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ErrorResponse handleConstraintViolationException(final ConstraintViolationException e) {
        log.warn(e.getMessage());
        return new ErrorResponse("Validation error", e.getMessage(), 400);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleRuntimeException(final Exception e) {
        log.warn(e.getMessage());
        return new ErrorResponse("Error", e.getMessage(), 500);
    }

}

