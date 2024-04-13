package ru.practicum.shareit.exception.exceptions;

public class ForbiddenAccessChangeStatusException extends RuntimeException {
    public ForbiddenAccessChangeStatusException(String message) {
        super(message);
    }
}
