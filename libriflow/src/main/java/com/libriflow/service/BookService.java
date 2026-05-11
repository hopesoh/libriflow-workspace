package com.libriflow.service;

import com.libriflow.model.Book;
import com.libriflow.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BookService {

    @Autowired
    private BookRepository bookRepository;

    public List<Book> findAll() {
        return bookRepository.findAll();
    }

    public Optional<Book> findById(Long id) {
        return bookRepository.findById(id);
    }

    public Book save(Book book) {
        return bookRepository.save(book);
    }

    public Book update(Long id, Book dados) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Livro não encontrado: " + id));
        book.setTitle(dados.getTitle());
        book.setAuthor(dados.getAuthor());
        book.setIsbn(dados.getIsbn());
        book.setPrice(dados.getPrice());
        book.setStock(dados.getStock());
        return bookRepository.save(book);
    }

    public void delete(Long id) {
        bookRepository.deleteById(id);
    }
}
