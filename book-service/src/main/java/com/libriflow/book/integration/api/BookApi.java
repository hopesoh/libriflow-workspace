package com.libriflow.book.integration.api;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

public interface BookApi {

    boolean checkBookExists(@PathVariable("bookId") Long bookId);
    BookDetailsDTO getBookDetails(@PathVariable Long bookId);
    void update(@PathVariable Long bookId, @RequestBody BookDetailsDTO book);
}
