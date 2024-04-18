package ru.practicum.exception;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import javax.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ErrorHandler {
    @ExceptionHandler({ValidationException.class, MissingRequestHeaderException.class,
        MethodArgumentNotValidException.class, ConstraintViolationException.class,
        MissingServletRequestParameterException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleValidation(final Exception e) {
        ApiError error = new ApiError();

        error.setErrors(Collections.emptyList());
        error.setMessage(e.getMessage());
        error.setReason("Bad Request");
        error.setStatus(HttpStatus.BAD_REQUEST);
        error.setTimestamp(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        return error;
    }

    @ExceptionHandler({ConflictException.class})
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleConflict(final ConflictException e) {
        ApiError error = new ApiError();;

        error.setMessage(e.getMessage());
        error.setStatus(HttpStatus.CONFLICT);
        error.setTimestamp(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        return error;
    }

    @ExceptionHandler({NotFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleWrongOwner(final NotFoundException e) {
        ApiError error = new ApiError();

        error.setErrors(Collections.emptyList());
        error.setMessage(e.getMessage());
        error.setReason("Not found");
        error.setStatus(HttpStatus.NOT_FOUND);
        error.setTimestamp(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        return error;
    }

    @ExceptionHandler({Exception.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiError handleException(final Throwable e) {
        ApiError error = new ApiError();

        StringWriter writer = new StringWriter();
        e.printStackTrace(new PrintWriter(writer));
        String stackTrace = writer.toString();

        error.setErrors(Collections.singletonList(stackTrace));
        error.setMessage(e.getMessage());
        error.setReason("Internal Server Error");
        error.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        error.setTimestamp(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        return error;
    }
}
