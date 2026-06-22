package com.libriflow.book.controller.advice;

import com.libriflow.book.exception.BookAlreadyExistsException;
import com.libriflow.book.exception.BookNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.ResponseEntity;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void bookNotFound_returnsStandardErrorBody() {
        HttpServletRequest request = request("/api/books/10");

        ResponseEntity<?> response = handler.handleBookNotFound(new BookNotFoundException(10L), request);

        assertThat(response.getStatusCode().value()).isEqualTo(404);
        assertThat(response.getBody()).isNotNull();
    }

    @Test
    void constraintViolation_returnsValidationPayload() {
        HttpServletRequest request = request("/api/books");
        ConstraintViolation<?> violation = Mockito.mock(ConstraintViolation.class);
        Path path = Mockito.mock(Path.class);
        when(path.toString()).thenReturn("saveBook.price");
        when(violation.getPropertyPath()).thenReturn(path);
        when(violation.getMessage()).thenReturn("must be greater than or equal to zero");

        ResponseEntity<?> response = handler.handleConstraintViolation(new ConstraintViolationException(Set.of(violation)), request);

        assertThat(response.getStatusCode().value()).isEqualTo(400);
        assertThat(response.getBody()).isNotNull();
    }

    @Test
    void bookAlreadyExists_returnsConflict() {
        HttpServletRequest request = request("/api/books");

        ResponseEntity<?> response = handler.handleBookAlreadyExists(new BookAlreadyExistsException("9780132350884", "title"), request);

        assertThat(response.getStatusCode().value()).isEqualTo(409);
        assertThat(response.getBody()).isNotNull();
    }

    private HttpServletRequest request(String path) {
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        when(request.getRequestURI()).thenReturn(path);
        return request;
    }
}
