package com.libriflow.order.controller.advice;

import com.libriflow.order.controller.error.ApiErrorResponse;
import com.libriflow.order.exception.BookNotFoundException;
import com.libriflow.order.exception.BookOutOfStockException;
import com.libriflow.order.exception.InvalidPurchaseRequestException;
import com.libriflow.order.exception.OrderNotFoundException;
import com.libriflow.order.exception.UserNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({
            OrderNotFoundException.class,
            UserNotFoundException.class,
            BookNotFoundException.class
    })
    public ResponseEntity<ApiErrorResponse> handleNotFound(RuntimeException exception, HttpServletRequest request) {
        return build(HttpStatus.NOT_FOUND, "RESOURCE_NOT_FOUND", exception.getMessage(), request.getRequestURI(), List.of());
    }

    @ExceptionHandler(BookOutOfStockException.class)
    public ResponseEntity<ApiErrorResponse> handleBookOutOfStock(BookOutOfStockException exception, HttpServletRequest request) {
        return build(HttpStatus.CONFLICT, "BOOK_OUT_OF_STOCK", exception.getMessage(), request.getRequestURI(), List.of());
    }

    @ExceptionHandler(InvalidPurchaseRequestException.class)
    public ResponseEntity<ApiErrorResponse> handleInvalidPurchaseRequest(InvalidPurchaseRequestException exception, HttpServletRequest request) {
        return build(HttpStatus.BAD_REQUEST, "INVALID_PURCHASE_REQUEST", exception.getMessage(), request.getRequestURI(), List.of());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleMethodArgumentNotValid(MethodArgumentNotValidException exception, HttpServletRequest request) {
        List<String> details = exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(this::formatFieldError)
                .toList();
        return build(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", "Requisição inválida.", request.getRequestURI(), details);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiErrorResponse> handleConstraintViolation(ConstraintViolationException exception, HttpServletRequest request) {
        List<String> details = exception.getConstraintViolations()
                .stream()
                .map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
                .toList();
        return build(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", "Requisição inválida.", request.getRequestURI(), details);
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ApiErrorResponse> handleResponseStatusException(ResponseStatusException exception, HttpServletRequest request) {
        HttpStatus status = HttpStatus.valueOf(exception.getStatusCode().value());
        String message = exception.getReason() != null ? exception.getReason() : status.getReasonPhrase();
        return build(status, "REQUEST_ERROR", message, request.getRequestURI(), List.of());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleGenericException(Exception exception, HttpServletRequest request) {
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_ERROR", "Erro inesperado.", request.getRequestURI(), List.of());
    }

    private ResponseEntity<ApiErrorResponse> build(HttpStatus status, String code, String message, String path, List<String> details) {
        ApiErrorResponse body = new ApiErrorResponse(
                Instant.now(),
                status.value(),
                status.getReasonPhrase(),
                message,
                path,
                code,
                details
        );
        return ResponseEntity.status(status).body(body);
    }

    private String formatFieldError(FieldError error) {
        return error.getField() + ": " + error.getDefaultMessage();
    }
}
