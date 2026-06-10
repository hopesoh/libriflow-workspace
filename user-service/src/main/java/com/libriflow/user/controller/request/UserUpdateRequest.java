package com.libriflow.user.controller.request;

import com.libriflow.user.validation.ValidEmail;
import com.libriflow.user.validation.ValidName;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserUpdateRequest(
        @NotBlank(message = "Name is required")
        @ValidName
        String name,
        @NotBlank(message = "Email is required")
        @ValidEmail
        String email,
        @NotBlank(message = "Password is required")
        @Size(min = 8, message = "Password must have at least 8 characters")
        String password
) {
}
