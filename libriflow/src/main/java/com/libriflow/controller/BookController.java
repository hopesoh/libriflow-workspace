package com.libriflow.controller;

import com.libriflow.model.Book;
import com.libriflow.repository.BookRepository;
import com.libriflow.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/books")
public class BookController {

    @Autowired
    private BookService bookService;

    // Injeção direta do repository no controller - viola a camada de serviço
    @Autowired
    private BookRepository bookRepository;

    @GetMapping
    public List<Book> findAll() {
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
        bookRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public List<Book> search(@RequestParam(required = false) String title,
                             @RequestParam(required = false) String author) {
        if (title != null) {
            // Acessa repository diretamente no controller, ignorando o service
            return bookRepository.findByTitleContainingIgnoreCase(title);
        }
        if (author != null) {
            return bookRepository.findByAuthorContainingIgnoreCase(author);
        }
        return bookService.findAll();
    }

    @GetMapping("/by-price")
    public List<Book> findByMaxPrice(@RequestParam BigDecimal max) {
        return bookRepository.findByPriceLessThanEqual(max);
    }

    @GetMapping("/in-stock")
    public List<Book> findInStock() {
        return bookRepository.findAllInStock();
    }
}
