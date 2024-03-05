package ru.practicum.shareit.exception.exceptions;

public class ForbiddenUpdateException extends RuntimeException {
    public ForbiddenUpdateException(String message) {
        super(message);
    }
}
