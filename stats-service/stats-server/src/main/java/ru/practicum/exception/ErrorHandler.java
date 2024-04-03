package ru.practicum.exception;

import javax.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {
    @ExceptionHandler({MissingRequestHeaderException.class, MethodArgumentNotValidException.class,
        ConstraintViolationException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorDto handleValidation(final Exception e) {
        log.error("error = {}, httpStatus = {}", e.getMessage(), HttpStatus.BAD_REQUEST);
        return new ErrorDto(e.getMessage());
    }

    @ExceptionHandler({Exception.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorDto handleException(final Throwable e) {
        log.error("error = {}, httpStatus = {}", e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        return new ErrorDto(e.getMessage());
    }
}
