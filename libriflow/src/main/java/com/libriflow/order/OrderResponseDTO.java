package com.libriflow.order;

import java.math.BigDecimal;

public record OrderResponseDTO(Long id, BigDecimal totalAmount, String name, String email) {}
