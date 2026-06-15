package com.libriflow.book.controller.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

public record BookCreateRequest(
        @NotBlank(message = "Title is required")
        String title,
        @NotBlank(message = "Author is required")
        String author,
        @NotBlank(message = "ISBN is required")
        String isbn,
        @NotNull(message = "Price is required")
        @PositiveOrZero(message = "Price must be greater than or equal to zero")
        BigDecimal price,
        @NotNull(message = "Stock is required")
        @PositiveOrZero(message = "Stock must be greater than or equal to zero")
        Integer stock
) {
}
