package com.libriflow.book.exception;

public class BookNotFoundException extends RuntimeException {

    public BookNotFoundException(Long id) {
        super("Livro não encontrado: " + id);
    }
}
