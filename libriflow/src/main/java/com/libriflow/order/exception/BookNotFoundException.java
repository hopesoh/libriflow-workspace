package com.libriflow.order.exception;

public class BookNotFoundException extends RuntimeException {

    public BookNotFoundException(Long id) {
        super("Livro não encontrado com id: " + id);
    }
}
