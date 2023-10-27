package ru.practicum.ewm.common.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import javax.validation.ConstraintViolationException;

@Slf4j
@RestControllerAdvice(basePackages = "ru.practicum.ewm")
public class ErrorHandler {
    @ExceptionHandler({BadRequestException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleBadRequest(EwmException e) {
        String reason = e.getReason();
        log.error(reason);
        return new ApiError(e);
    }

    @ExceptionHandler({MethodArgumentNotValidException.class,
            ConstraintViolationException.class, MethodArgumentTypeMismatchException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleValidate(Throwable e) {
        String reason = "Некорректные параметры запроса";
        log.error(reason);
        return new ApiError(e, reason, "BAD_REQUEST");
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleNotFound(NotFoundException e) {
        String reason = e.getReason();
        log.error(reason);
        return new ApiError(e);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleConflict(ConflictException e) {
        String reason = e.getReason();
        log.error(reason);
        return new ApiError(e);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleDataIntegrityViolation(DataIntegrityViolationException e) {
        String reason = EwmException.INTEGRITY_VIOLATION_REASON;
        log.error(reason);
        return new ApiError(e, reason, "CONFLICT");
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ApiError handleForbidden(ForbiddenException e) {
        String reason = e.getReason();
        log.error(reason);
        return new ApiError(e);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiError handleUnexpectedError(RuntimeException e) {
        String reason = EwmException.INTERNAL_ERROR_REASON;
        log.error(reason);
        return new ApiError(e, reason, "INTERNAL_SERVER_ERROR");
    }
}
