package ru.yandex.practicum.filmorate.configs;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;

import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@ControllerAdvice
public class AppExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler
    public ResponseEntity<Object> notFoundExceptionHandler(NotFoundException ex, WebRequest request) {
        ProblemDetail problemDetail = createProblemDetail(
                ex,
                HttpStatus.NOT_FOUND,
                ex.getMessage(),
                null, null,
                request);
        log.trace(ex.getMessage());
        return handleExceptionInternal(ex, problemDetail, new HttpHeaders(), HttpStatus.NOT_FOUND, request);
    }

    @ExceptionHandler
    public ResponseEntity<Object> handleAllUncaughtException(RuntimeException ex, WebRequest request) {
        ProblemDetail problemDetail = createProblemDetail(
                ex,
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Внутренняя ошибка",
                null, null,
                request);
        log.error(ex.getMessage());
        return handleExceptionInternal(ex, problemDetail, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, request);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        ProblemDetail problemDetail = ex.getBody();
        Map<String, String> errors = ex.getFieldErrors()
                .stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        f -> f.getDefaultMessage() != null ? f.getDefaultMessage() : ""));
        problemDetail.setProperty("errors", errors);
        log.warn(ex.getMessage());
        return handleExceptionInternal(ex, problemDetail, headers, status, request);
    }
}
