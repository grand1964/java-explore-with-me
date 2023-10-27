package ru.practicum.ewm.common.exception;

public class ConflictException extends EwmException {

    public ConflictException(String message) {
        super("CONFLICT", message, CONFLICT_REASON);
    }
}
