package com.libriflow.book.controller;

import com.libriflow.book.controller.request.BookCreateRequest;
import com.libriflow.book.controller.request.BookUpdateRequest;
import com.libriflow.book.entity.Book;
import com.libriflow.book.exception.BookNotFoundException;
import com.libriflow.book.integration.BookIntegrationService;
import com.libriflow.book.integration.api.BookDetailsDTO;
import com.libriflow.book.service.BookService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@Validated
@RequestMapping("/api/books")
public class BookController {

    private final BookService bookService;
    private final BookIntegrationService bookIntegrationService;

    public BookController(BookService bookService, BookIntegrationService bookIntegrationService) {
        this.bookService = bookService;
        this.bookIntegrationService = bookIntegrationService;
    }

    @GetMapping
    public List<BookDetailsDTO> findAll() {
        return bookService
                .findAll()
                .stream()
                .map(book -> new BookDetailsDTO(book.getId(), book.getTitle(), book.getAuthor(), book.getIsbn(), book.getPrice(), book.getStock()))
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookDetailsDTO> findById(@PathVariable @Positive Long id) {
        return bookService.findById(id)
                .map(book -> ResponseEntity.ok(new BookDetailsDTO(book.getId(), book.getTitle(), book.getAuthor(), book.getIsbn(), book.getPrice(), book.getStock())))
                .orElseThrow(() -> new BookNotFoundException(id));
    }

    @PostMapping
    public ResponseEntity<BookDetailsDTO> save(@Valid @RequestBody BookCreateRequest request) {
        Book savedBook = bookService.save(request);
        return ResponseEntity.status(201).body(toDetailsDto(savedBook));
    }

    @PutMapping("/{id}")
    public ResponseEntity<BookDetailsDTO> update(@PathVariable @Positive Long id, @Valid @RequestBody BookUpdateRequest request) {
        Book updatedBook = bookService.update(id, request);
        return ResponseEntity.ok(toDetailsDto(updatedBook));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable @Positive Long id) {
        bookService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public List<BookDetailsDTO> search(@RequestParam(required = false) String title,
                                       @RequestParam(required = false) String author) {
        return bookService.findBy(title, author).stream().map(BookController::toDetailsDto).toList();
    }

    @GetMapping("/by-price")
    public List<BookDetailsDTO> findByMaxPrice(@RequestParam @PositiveOrZero BigDecimal max) {
        return bookService.findByPriceLessThanEqual(max).stream().map(BookController::toDetailsDto).toList();
    }

    @GetMapping("/in-stock")
    public List<BookDetailsDTO> findInStock() {
        return bookService.findAllInStock().stream().map(BookController::toDetailsDto).toList();
    }

    @GetMapping("/{id}/exists")
    boolean checkBookExists(@PathVariable @Positive Long id) {
        return bookIntegrationService.checkBookExists(id);
    }

    @GetMapping("/{id}/details")
    BookDetailsDTO getBookDetails(@PathVariable @Positive Long id) {
        return bookIntegrationService.getBookDetails(id);
    }

    private static BookDetailsDTO toDetailsDto(Book book) {
        return new BookDetailsDTO(book.getId(), book.getTitle(), book.getAuthor(), book.getIsbn(), book.getPrice(), book.getStock());
    }
}
