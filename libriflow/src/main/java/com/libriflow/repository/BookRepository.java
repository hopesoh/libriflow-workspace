package com.libriflow.repository;

import com.libriflow.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface BookRepository extends JpaRepository<Book, Long> {

    List<Book> findByTitleContainingIgnoreCase(String title);

    List<Book> findByAuthorContainingIgnoreCase(String author);

    // Query nativa desnecessária - o JPA já faria isso com derived query
    @Query(value = "SELECT * FROM books WHERE price <= :maxPrice ORDER BY price ASC", nativeQuery = true)
    List<Book> findByPriceLessThanEqual(@Param("maxPrice") BigDecimal maxPrice);

    // Duplica o que findByStockGreaterThan já faria - redundância proposital
    @Query("SELECT b FROM Book b WHERE b.stock > 0")
    List<Book> findAllInStock();
}
