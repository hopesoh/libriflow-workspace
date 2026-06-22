package com.libriflow.order.exception;

public class OrderNotFoundException extends RuntimeException {

    public OrderNotFoundException(Long id) {
        super("Pedido não encontrado com id: " + id);
    }
}
