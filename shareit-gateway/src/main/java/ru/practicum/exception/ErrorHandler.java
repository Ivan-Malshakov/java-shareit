package ru.practicum.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

import javax.validation.ValidationException;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handlerValidationException(final ValidationException e) {
        log.error(e.getMessage());
        return new ErrorResponse("Validation error", e.getMessage(), 400);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handlerHttpClientErrorException(final HttpClientErrorException e) {
        log.error(e.getMessage());
        return new ErrorResponse("Validation error", e.getMessage(), 400);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handlerHttpServerErrorException(final HttpServerErrorException e) {
        log.error(e.getMessage());
        return new ErrorResponse("Validation error", e.getMessage(), 500);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handlerDataAccessException(final DataAccessException e) {
        log.error(e.getMessage());
        return new ErrorResponse("Validation error", e.getMessage(), 500);
    }

}

