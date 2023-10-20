package ru.practicum.ewm.common.exception;

import lombok.Getter;
import ru.practicum.ewm.common.convert.TimeConverter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class ApiError {
    private final List<String> errors;
    private final String reason;
    private final String message;
    private final String status;
    private final String timestamp;

    //закрытый конструктор для пользовательских исключений
    private ApiError(Throwable e, String reason, String status, String message) {
        errors = Arrays.stream(e.getStackTrace())
                .map(StackTraceElement::toString)
                .collect(Collectors.toList());
        this.reason = reason;
        this.message = message;
        this.status = status;
        timestamp = TimeConverter.formatNow();
    }

    //открытый конструктор для пользовательских исключений
    public ApiError(EwmException e) {
        this(e, e.getReason(), e.getStatus(), e.getMessage());
    }

    //конструктор для внешних исключений
    public ApiError(Throwable e, String reason, String status) {
        this(e, reason, status, e.getMessage());
    }
}
