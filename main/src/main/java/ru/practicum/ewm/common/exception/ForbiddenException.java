package ru.practicum.ewm.common.exception;

public class ForbiddenException extends EwmException {

    public ForbiddenException(String message) {
        super("FORBIDDEN", message, FORBIDDEN_REASON);
    }
}
