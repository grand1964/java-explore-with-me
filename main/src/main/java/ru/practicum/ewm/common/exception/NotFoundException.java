package ru.practicum.ewm.common.exception;

public class NotFoundException extends EwmException {
    public NotFoundException(String message) {
        super("NOT_FOUND", message, NOT_FOUND_REASON);
    }
}
