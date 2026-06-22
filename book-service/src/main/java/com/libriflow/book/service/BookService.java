package com.libriflow.book.service;

import com.libriflow.book.controller.request.BookCreateRequest;
import com.libriflow.book.controller.request.BookUpdateRequest;
import com.libriflow.book.entity.Book;
import com.libriflow.book.exception.BookAlreadyExistsException;
import com.libriflow.book.exception.BookNotFoundException;
import com.libriflow.book.repository.BookRepository;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class BookService {

    private final BookRepository bookRepository;

    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public List<Book> findAll() {
        return bookRepository.findAll();
    }

    public Optional<Book> findById(Long id) {
        return bookRepository.findById(id);
    }

    public Book save(BookCreateRequest request) {
        String title = normalize(request.title());
        String author = normalize(request.author());
        String isbn = normalize(request.isbn());

        ensureBookCombinationAvailable(isbn, author, title, null);

        Book book = new Book();
        book.setTitle(title);
        book.setAuthor(author);
        book.setIsbn(isbn);
        book.setPrice(request.price());
        book.setStock(request.stock());
        return bookRepository.save(book);
    }

    public Book update(Long id, BookUpdateRequest request) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new BookNotFoundException(id));

        String title = normalize(request.title());
        String author = normalize(request.author());
        String isbn = normalize(request.isbn());

        ensureBookCombinationAvailable(isbn, author, title, id);

        book.setTitle(title);
        book.setAuthor(author);
        book.setIsbn(isbn);
        book.setPrice(request.price());
        book.setStock(request.stock());
        return bookRepository.save(book);
    }

    public void delete(Long id) {
        if (!bookRepository.existsById(id)) {
            throw new BookNotFoundException(id);
        }
        bookRepository.deleteById(id);
    }

    public boolean existsBy(Long id) {
        return bookRepository.existsById(id);
    }

    public List<Book> findBy(String title, String author) {
        String normalizedTitle = normalizeSearchTerm(title);
        String normalizedAuthor = normalizeSearchTerm(author);

        if (normalizedTitle != null && normalizedAuthor != null) {
            return bookRepository.findByTitleContainingIgnoreCaseOrAuthorContainingIgnoreCase(normalizedTitle, normalizedAuthor);
        }
        if (normalizedTitle != null) {
            return bookRepository.findByTitleContainingIgnoreCase(normalizedTitle);
        }
        if (normalizedAuthor != null) {
            return bookRepository.findByAuthorContainingIgnoreCase(normalizedAuthor);
        }
        return findAll();
    }

    public List<Book> findByPriceLessThanEqual(BigDecimal max) {
        return bookRepository.findByPriceLessThanEqual(max);
    }

    public List<Book> findAllInStock() {
        return bookRepository.findByStockGreaterThan(0);
    }

    private void ensureBookCombinationAvailable(String isbn, String author, String title, Long bookId) {
        BookIdentity identity = new BookIdentity(isbn, author, title, bookId);

        if (existsByIsbnAndAuthor(identity)) {
            throw new BookAlreadyExistsException(isbn, "author");
        }
        if (existsByIsbnAndTitle(identity)) {
            throw new BookAlreadyExistsException(isbn, "title");
        }
    }

    private boolean existsByIsbnAndAuthor(BookIdentity identity) {
        if (identity.bookId() == null) {
            return bookRepository.existsByIsbnAndAuthor(identity.isbn(), identity.author());
        }
        return bookRepository.existsByIsbnAndAuthorAndIdNot(identity.isbn(), identity.author(), identity.bookId());
    }

    private boolean existsByIsbnAndTitle(BookIdentity identity) {
        if (identity.bookId() == null) {
            return bookRepository.existsByIsbnAndTitle(identity.isbn(), identity.title());
        }
        return bookRepository.existsByIsbnAndTitleAndIdNot(identity.isbn(), identity.title(), identity.bookId());
    }

    private String normalize(String value) {
        return value.trim().replaceAll("\\s+", " ");
    }

    private String normalizeSearchTerm(String value) {
        if (!StringUtils.hasText(value)) {
            return null;
        }
        return value.trim();
    }

    private record BookIdentity(String isbn, String author, String title, Long bookId) {
    }
}
