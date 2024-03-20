package ru.practicum.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import javax.validation.ValidationException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.NoSuchElementException;

import static org.springframework.http.HttpStatus.*;

@RestControllerAdvice
@Slf4j
public class ErrorHandler {

    @ExceptionHandler({ValidationException.class, MethodArgumentNotValidException.class, IllegalArgumentException.class,
            HttpMessageNotReadableException.class, MethodArgumentTypeMismatchException.class,
            MissingServletRequestParameterException.class, IllegalStateException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorInfo handleBadRequestException(Exception e) {
        return new ErrorInfo(BAD_REQUEST.name(), BAD_REQUEST.getReasonPhrase(), e.getMessage(), LocalDateTime.now());
    }

    @ExceptionHandler({SecurityException.class, ValidationConflictException.class,
            ConstraintViolationException.class, DateTimeParseException.class,
            DataIntegrityViolationException.class})
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorInfo handleConflictException(Exception e) {
        return new ErrorInfo(CONFLICT.name(), CONFLICT.getReasonPhrase(), e.getMessage(), LocalDateTime.now());
    }

    @ExceptionHandler({NotFoundException.class, NoSuchElementException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorInfo handleNoSuchElementException(Exception e) {
        return new ErrorInfo(NOT_FOUND.name(), NOT_FOUND.getReasonPhrase(), e.getMessage(), LocalDateTime.now());
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorInfo processException(Exception e) {
        log.error("Unexpected error: ", e);
        return new ErrorInfo(INTERNAL_SERVER_ERROR.name(), INTERNAL_SERVER_ERROR.getReasonPhrase(), e.getMessage(),
                LocalDateTime.now());
    }

    @RequiredArgsConstructor
    @Getter
    public static class ErrorInfo {

        private String status;
        private String reason;
        private String message;
        private String timestamp;

        public ErrorInfo(String status, String reason, String message, LocalDateTime timestamp) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

            this.status = status;
            this.reason = reason;
            this.message = message;
            this.timestamp = formatter.format(timestamp);
        }
    }
}