package ru.yandex.practicum.filmorate.configs;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import ru.yandex.practicum.filmorate.dto.ProblemDetail;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;

import javax.validation.ConstraintViolationException;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@ControllerAdvice
public class AppExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler
    public ResponseEntity<Object> notFoundExceptionHandler(NotFoundException ex, WebRequest request) {
        ProblemDetail problemDetail = new ProblemDetail(
                HttpStatus.NOT_FOUND,
                ((ServletWebRequest) request).getRequest().getRequestURI(),
                ex.getMessage());
        log.trace(ex.getMessage());
        return handleExceptionInternal(ex, problemDetail, new HttpHeaders(), HttpStatus.NOT_FOUND, request);
    }

    @ExceptionHandler
    public ResponseEntity<Object> handleConstraintViolationException(ConstraintViolationException ex, WebRequest request) {
        ProblemDetail problemDetail = new ProblemDetail(
                HttpStatus.BAD_REQUEST,
                ((ServletWebRequest) request).getRequest().getRequestURI(),
                "Ошибка валидации");
        Map<String, String> errors = ex.getConstraintViolations()
                .stream()
                .collect(Collectors.toMap(
                        f -> f.getPropertyPath().toString(),
                        f -> f.getMessage() != null ? f.getMessage() : ""));
        problemDetail.setProperty("errors", errors);
        log.warn(ex.getMessage());
        return handleExceptionInternal(ex, problemDetail, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler
    public ResponseEntity<Object> handleAllUncaughtException(RuntimeException ex, WebRequest request) {
        ProblemDetail problemDetail = new ProblemDetail(
                HttpStatus.INTERNAL_SERVER_ERROR,
                ((ServletWebRequest) request).getRequest().getRequestURI());
        log.error(ex.getMessage());
        return handleExceptionInternal(ex, problemDetail, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, request);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        ProblemDetail problemDetail = new ProblemDetail(
                HttpStatus.BAD_REQUEST,
                ((ServletWebRequest) request).getRequest().getRequestURI(),
                "Ошибка валидации");
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
