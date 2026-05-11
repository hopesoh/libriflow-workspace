package com.libriflow.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "books")
@Getter
@Setter
@NoArgsConstructor
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String author;
    private String isbn;
    private BigDecimal price;
    private Integer stock;

    // EAGER em coleção - carrega TODOS os pedidos ao buscar qualquer livro
    @ManyToMany(mappedBy = "books", fetch = FetchType.EAGER)
    @JsonIgnore
    private List<Order> orders = new ArrayList<>();
}
