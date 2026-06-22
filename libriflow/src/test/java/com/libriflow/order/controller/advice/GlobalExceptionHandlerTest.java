package com.libriflow.order.controller.advice;

import com.libriflow.order.exception.BookOutOfStockException;
import com.libriflow.order.exception.InvalidPurchaseRequestException;
import com.libriflow.order.exception.OrderNotFoundException;
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
    void orderNotFound_returnsStandardErrorBody() {
        HttpServletRequest request = request("/api/orders/9");

        ResponseEntity<?> response = handler.handleNotFound(new OrderNotFoundException(9L), request);

        assertThat(response.getStatusCode().value()).isEqualTo(404);
        assertThat(response.getBody()).isNotNull();
    }

    @Test
    void invalidPurchaseRequest_returnsBadRequest() {
        HttpServletRequest request = request("/api/orders/purchase/1");

        ResponseEntity<?> response = handler.handleInvalidPurchaseRequest(
                new InvalidPurchaseRequestException("A lista de livros não pode ser vazia."),
                request
        );

        assertThat(response.getStatusCode().value()).isEqualTo(400);
        assertThat(response.getBody()).isNotNull();
    }

    @Test
    void constraintViolation_returnsValidationPayload() {
        HttpServletRequest request = request("/api/orders");
        ConstraintViolation<?> violation = Mockito.mock(ConstraintViolation.class);
        Path path = Mockito.mock(Path.class);
        when(path.toString()).thenReturn("purchase.userId");
        when(violation.getPropertyPath()).thenReturn(path);
        when(violation.getMessage()).thenReturn("must be greater than 0");

        ResponseEntity<?> response = handler.handleConstraintViolation(new ConstraintViolationException(Set.of(violation)), request);

        assertThat(response.getStatusCode().value()).isEqualTo(400);
        assertThat(response.getBody()).isNotNull();
    }

    @Test
    void bookOutOfStock_returnsConflict() {
        HttpServletRequest request = request("/api/orders/purchase/1");

        ResponseEntity<?> response = handler.handleBookOutOfStock(new BookOutOfStockException(10L, "Clean Code"), request);

        assertThat(response.getStatusCode().value()).isEqualTo(409);
        assertThat(response.getBody()).isNotNull();
    }

    private HttpServletRequest request(String path) {
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        when(request.getRequestURI()).thenReturn(path);
        return request;
    }
}
