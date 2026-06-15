package com.libriflow.book.repository;

import com.libriflow.book.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigDecimal;
import java.util.List;

public interface BookRepository extends JpaRepository<Book, Long> {

    List<Book> findByTitleContainingIgnoreCase(String title);

    List<Book> findByAuthorContainingIgnoreCase(String author);

    List<Book> findByTitleContainingIgnoreCaseOrAuthorContainingIgnoreCase(String title, String author);

    List<Book> findByPriceLessThanEqual(BigDecimal maxPrice);

    List<Book> findByStockGreaterThan(Integer stock);

    boolean existsByIsbnAndAuthor(String isbn, String author);

    boolean existsByIsbnAndTitle(String isbn, String title);

    boolean existsByIsbnAndAuthorAndIdNot(String isbn, String author, Long id);

    boolean existsByIsbnAndTitleAndIdNot(String isbn, String title, Long id);

}
