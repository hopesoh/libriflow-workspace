package com.libriflow.book.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class BookAlreadyExistsException extends RuntimeException {

    public BookAlreadyExistsException(String isbn, String field) {
        super("Book already exists for isbn " + isbn + " and " + field);
    }
}
