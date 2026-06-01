package com.libriflow.order.integration.book;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

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
