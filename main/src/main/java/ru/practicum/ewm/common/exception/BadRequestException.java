package ru.practicum.ewm.common.exception;

public class BadRequestException extends EwmException {

    public BadRequestException(String message) {
        super("BAD_REQUEST", message, BAD_REQUEST_REASON);
    }
}
