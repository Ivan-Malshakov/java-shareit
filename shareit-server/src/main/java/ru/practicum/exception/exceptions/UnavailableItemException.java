package ru.practicum.exception.exceptions;

public class UnavailableItemException extends RuntimeException {
    public UnavailableItemException(String message) {
        super(message);
    }
}
