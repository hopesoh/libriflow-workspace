package com.libriflow.order.entity;

import com.libriflow.order.integration.book.BookDetailsDTO;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    @ElementCollection
    @CollectionTable(name = "order_books", joinColumns = @JoinColumn(name = "order_id"))
    @Column(name = "book_id")
    private List<Long> bookIds;

    private BigDecimal total;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    private String status;
}
