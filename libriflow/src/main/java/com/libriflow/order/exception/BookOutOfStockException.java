package com.libriflow.order.exception;

public class BookOutOfStockException extends RuntimeException {

    public BookOutOfStockException(Long id, String title) {
        super("Livro sem estoque: \"" + title + "\" (id=" + id + ")");
    }
}
