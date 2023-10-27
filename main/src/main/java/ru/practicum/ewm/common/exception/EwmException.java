package ru.practicum.ewm.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public abstract class EwmException extends RuntimeException {
    public static final String BAD_REQUEST_REASON = "Неверно составлен запрос";
    public static final String NOT_FOUND_REASON = "Запрашиваемый объект не найден";
    public static final String CONFLICT_REASON = "Обнаружен конфликт";
    public static final String INTEGRITY_VIOLATION_REASON = "Нарушено условие целостности данных";
    public static final String FORBIDDEN_REASON = "Действие запрещено";
    public static final String INTERNAL_ERROR_REASON = "Произошла непредвиденная ошибка";
    private String status;
    private String message;
    private String reason;
}
