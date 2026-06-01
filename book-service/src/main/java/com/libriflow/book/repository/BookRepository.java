package com.libriflow.book.repository;

import com.libriflow.book.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface BookRepository extends JpaRepository<Book, Long> {

    List<Book> findByTitleContainingIgnoreCase(String title);

    List<Book> findByAuthorContainingIgnoreCase(String author);

    List<Book> findByPriceLessThanEqual(BigDecimal maxPrice);

    List<Book> findByStockGreaterThan(BigDecimal maxPrice);

}
