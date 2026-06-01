package com.libriflow.book.controller;

import com.libriflow.book.entity.Book;
import com.libriflow.book.integration.BookIntegrationService;
import com.libriflow.book.integration.api.BookDetailsDTO;
import com.libriflow.book.service.BookService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/books")
public class BookController {

    private BookService bookService;
    private BookIntegrationService bookIntegrationService;

    public BookController(BookService bookService, BookIntegrationService bookIntegrationService) {
        this.bookService = bookService;
        this.bookIntegrationService = bookIntegrationService;
    }

    @GetMapping
    public List<Book> findAll() {
        // Retorna todos os usuários incluindo o campo password - expõe dados sensíveis
        return bookService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Book> findById(@PathVariable Long id) {
        return bookService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Book save(@RequestBody Book book) {
        // Recebe entidade JPA diretamente do JSON - sem DTO, sem validação
        return bookService.save(book);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Book> update(@PathVariable Long id, @RequestBody Book book) {
        try {
            return ResponseEntity.ok(bookService.update(id, book));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        // Bypassa o service e chama o repository diretamente - inconsistente com os outros métodos
        bookService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public List<Book> search(@RequestParam(required = false) String title,
                             @RequestParam(required = false) String author) {
        return bookService.findBy(title, author);
    }

    @GetMapping("/by-price")
    public List<Book> findByMaxPrice(@RequestParam BigDecimal max) {
        return bookService.findByPriceLessThanEqual(max);
    }

    @GetMapping("/in-stock")
    public List<Book> findInStock() {
        return bookService.findAllInStock();
    }

    @GetMapping("/{id}/exists")
    boolean checkBookExists(@PathVariable Long id) {
        return bookIntegrationService.checkBookExists(id);
    }

    @GetMapping("/{id}/details")
    BookDetailsDTO getBookDetails(@PathVariable Long id) {
        return bookIntegrationService.getBookDetails(id);
    }
}
