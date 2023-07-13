package ru.practicum.main_service.exception;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import ru.practicum.stats.dto.Utils;

import javax.validation.ConstraintViolationException;
import java.time.LocalDateTime;
import java.util.Objects;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleValidationException(final MethodArgumentNotValidException exception) {
        log.error(exception.toString());
        return new ApiError(HttpStatus.BAD_REQUEST.name(),
                "Bad request.",
                String.format("Field: %s. Error: %s", Objects.requireNonNull(exception.getFieldError()).getField(),
                        exception.getFieldError().getDefaultMessage()),
                ExceptionUtils.getStackTrace(exception),
                LocalDateTime.now().format(Utils.DATE_FORMATTER));
    }

    @ExceptionHandler(ValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleValidationException(final ValidationException exception) {
        log.error(exception.toString());
        return new ApiError(HttpStatus.BAD_REQUEST.name(),
                "Bad request.",
                exception.getMessage(),
                ExceptionUtils.getStackTrace(exception),
                LocalDateTime.now().format(Utils.DATE_FORMATTER));
    }

    @ExceptionHandler({MethodArgumentTypeMismatchException.class, ConstraintViolationException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleValidationException(final RuntimeException exception) {
        log.error(exception.toString());
        return new ApiError(HttpStatus.BAD_REQUEST.name(),
                "Bad request.",
                exception.getMessage(),
                ExceptionUtils.getStackTrace(exception),
                LocalDateTime.now().format(Utils.DATE_FORMATTER));
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleNotFoundException(final NotFoundException exception) {
        log.error(exception.toString());
        return new ApiError(HttpStatus.NOT_FOUND.name(),
                "The object was not found.",
                exception.getMessage(),
                ExceptionUtils.getStackTrace(exception),
                LocalDateTime.now().format(Utils.DATE_FORMATTER));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleDataIntegrityViolationException(final DataIntegrityViolationException exception) {
        log.error(exception.toString());
        return new ApiError(HttpStatus.CONFLICT.name(),
                "Integrity constraint has been violated.",
                exception.getMessage(),
                ExceptionUtils.getStackTrace(exception),
                LocalDateTime.now().format(Utils.DATE_FORMATTER));
    }

    @ExceptionHandler(ForbiddenException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleForbiddenException(final ForbiddenException exception) {
        log.error(exception.toString());
        return new ApiError(HttpStatus.CONFLICT.name(),
                "Access restrictions.",
                exception.getMessage(),
                ExceptionUtils.getStackTrace(exception),
                LocalDateTime.now().format(Utils.DATE_FORMATTER));
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiError handleException(final RuntimeException exception) {
        log.error("Error 400: {}", exception.getMessage(), exception);
        return new ApiError(HttpStatus.INTERNAL_SERVER_ERROR.name(),
                "Internal server error.",
                exception.getMessage(),
                ExceptionUtils.getStackTrace(exception),
                LocalDateTime.now().format(Utils.DATE_FORMATTER));
    }
}
