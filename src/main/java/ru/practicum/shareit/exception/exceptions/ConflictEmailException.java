package ru.practicum.shareit.exception.exceptions;

public class ConflictEmailException extends RuntimeException {
    public ConflictEmailException(String message) {
        super(message);
    }
}
