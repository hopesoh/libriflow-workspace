package com.libriflow.book.integration;

import com.libriflow.book.entity.Book;
import com.libriflow.book.integration.api.BookApi;
import com.libriflow.book.integration.api.BookDetailsDTO;
import com.libriflow.book.repository.BookRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
public class BookIntegrationService implements BookApi {

    private final BookRepository bookRepository;

    public BookIntegrationService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @Override
    public boolean checkBookExists(Long bookId) {
        return bookRepository.existsById(bookId);
    }

    @Override
    public BookDetailsDTO getBookDetails(Long bookId) {
        return bookRepository
                .findById(bookId)
                .map(book -> new BookDetailsDTO(book.getId(), book.getTitle(), book.getAuthor(), book.getIsbn(), book.getPrice(), book.getStock()))
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Book not found"));
    }

    @Override
    public void update(Long bookId, BookDetailsDTO bookDetailsDTO) {
        Book book = bookRepository.findById(bookId).orElseThrow(() -> new RuntimeException("Book not found"));

        book.setTitle(bookDetailsDTO.getTitle());
        book.setAuthor(bookDetailsDTO.getAuthor());
        book.setIsbn(bookDetailsDTO.getIsbn());
        book.setPrice(bookDetailsDTO.getPrice());
        book.setStock(bookDetailsDTO.getStock());
        bookRepository.save(book);
    }
}
