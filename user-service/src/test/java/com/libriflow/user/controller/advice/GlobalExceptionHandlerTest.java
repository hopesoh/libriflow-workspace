package com.libriflow.user.controller.advice;

import com.libriflow.user.exception.EmailAlreadyInUseException;
import com.libriflow.user.exception.UserNotFoundException;
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
    void userNotFound_returnsStandardErrorBody() {
        HttpServletRequest request = request("/api/users/9");

        ResponseEntity<?> response = handler.handleUserNotFound(new UserNotFoundException(9L), request);

        assertThat(response.getStatusCode().value()).isEqualTo(404);
        assertThat(response.getBody()).isNotNull();
    }

    @Test
    void constraintViolation_returnsValidationPayload() {
        HttpServletRequest request = request("/api/users");
        ConstraintViolation<?> violation = Mockito.mock(ConstraintViolation.class);
        Path path = Mockito.mock(Path.class);
        when(path.toString()).thenReturn("createUser.email");
        when(violation.getPropertyPath()).thenReturn(path);
        when(violation.getMessage()).thenReturn("must not be blank");

        ResponseEntity<?> response = handler.handleConstraintViolation(new ConstraintViolationException(Set.of(violation)), request);

        assertThat(response.getStatusCode().value()).isEqualTo(400);
        assertThat(response.getBody()).isNotNull();
    }

    @Test
    void emailAlreadyInUse_returnsConflict() {
        HttpServletRequest request = request("/api/users");

        ResponseEntity<?> response = handler.handleEmailAlreadyInUse(new EmailAlreadyInUseException("mail@example.com"), request);

        assertThat(response.getStatusCode().value()).isEqualTo(409);
        assertThat(response.getBody()).isNotNull();
    }

    private HttpServletRequest request(String path) {
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        when(request.getRequestURI()).thenReturn(path);
        return request;
    }
}
