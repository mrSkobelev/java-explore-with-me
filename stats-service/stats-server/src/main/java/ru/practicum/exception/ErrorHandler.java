package ru.practicum.exception;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.util.Collections;
import javax.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {
    @ExceptionHandler({MissingRequestHeaderException.class, MethodArgumentNotValidException.class,
        ConstraintViolationException.class, MissingServletRequestParameterException.class,
        ValidationException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public StatsApiError handleValidation(final Exception e) {
        log.error("error = {}, httpStatus = {}", e.getMessage(), HttpStatus.BAD_REQUEST);

        return new StatsApiError(
            Collections.emptyList(),
            e.getMessage(),
            "Incorrectly made request.",
            HttpStatus.BAD_REQUEST,
            LocalDateTime.now()
        );
    }

    @ExceptionHandler({Exception.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public StatsApiError handleException(final Throwable e) {
        log.error("error = {}, httpStatus = {}", e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);

        StringWriter out = new StringWriter();
        e.printStackTrace(new PrintWriter(out));
        String stackTrace = out.toString();

        return new StatsApiError(
            Collections.singletonList(stackTrace),
            e.getMessage(),
            "The request was issued internal server error.",
            HttpStatus.INTERNAL_SERVER_ERROR,
            LocalDateTime.now()
        );
    }
}
