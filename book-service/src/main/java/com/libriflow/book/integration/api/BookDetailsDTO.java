package com.libriflow.book.integration.api;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@AllArgsConstructor
@Getter
@Setter
public class BookDetailsDTO {

    private Long id;
    private String title;
    private String author;
    private String isbn;
    private BigDecimal price;
    private Integer stock;

}
