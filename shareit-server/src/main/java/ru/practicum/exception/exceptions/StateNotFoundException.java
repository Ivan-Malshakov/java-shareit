package ru.practicum.exception.exceptions;

public class StateNotFoundException extends RuntimeException {
    public StateNotFoundException(String message) {
        super(message);
    }
}
