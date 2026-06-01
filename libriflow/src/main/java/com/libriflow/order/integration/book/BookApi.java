package com.libriflow.order.integration.book;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "book-service", url = "http://localhost:8082", fallback = BookApiFallback.class)
public interface BookApi {

    @GetMapping("/api/books/{bookId}/exists")
    boolean checkBookExists(@PathVariable("bookId") Long bookId);

    @GetMapping("/api/books/{bookId}/details")
    BookDetailsDTO getBookDetails(@PathVariable Long bookId);

    @PutMapping("/api/books/{bookId}")
    void update(@PathVariable Long bookId, @RequestBody BookDetailsDTO book);
}
