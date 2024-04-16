package ru.practicum.exception;

import java.util.List;
import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
public class ApiError {
    private List<String> errors;
    private String message;
    private String reason;
    private HttpStatus status;
    private String timestamp;
}
