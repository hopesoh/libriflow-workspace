package com.libriflow.order.integration.book;

import org.springframework.stereotype.Component;

@Component
public class BookApiFallback implements BookApi {
    @Override
    public boolean checkBookExists(Long bookId) {
        System.out.println("🛡️ CIRCUIT BREAKER: book-service fora do ar! Retornando false por segurança.");
        return false;
    }

    @Override
    public BookDetailsDTO getBookDetails(Long bookId) {
        System.out.println("🛡️ CIRCUIT BREAKER: book-service fora do ar! Retornando livro fantasma.");
        return new BookDetailsDTO();
    }

    @Override
    public void update(Long bookId, BookDetailsDTO book) {
        System.out.println("🛡️ CIRCUIT BREAKER: book-service fora do ar! Nenhum livro será atualizado.");
    }
}
