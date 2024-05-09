package ru.practicum.exception.exceptions;

public class ForbiddenUpdateException extends RuntimeException {
    public ForbiddenUpdateException(String message) {
        super(message);
    }
}
