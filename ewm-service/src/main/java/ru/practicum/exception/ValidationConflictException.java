package ru.practicum.exception;

public class ValidationConflictException extends RuntimeException {
    public ValidationConflictException(String message) {
        super(message);
    }
}
