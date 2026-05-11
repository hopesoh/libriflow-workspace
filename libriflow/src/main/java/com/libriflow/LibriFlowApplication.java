package com.libriflow;

import com.libriflow.model.Book;
import com.libriflow.repository.BookRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;

import java.math.BigDecimal;

@SpringBootApplication
@EnableFeignClients
public class LibriFlowApplication {

    public static void main(String[] args) {
        SpringApplication.run(LibriFlowApplication.class, args);
    }

    @Bean
    CommandLineRunner dataInitializer(BookRepository bookRepository) {
        return args -> {
            if (bookRepository.count() == 0) {
                Book b1 = new Book();
                b1.setTitle("O Senhor dos Anéis");
                b1.setAuthor("J.R.R. Tolkien");
                b1.setIsbn("978-8533613379");
                b1.setPrice(new BigDecimal("79.90"));
                b1.setStock(10);
                bookRepository.save(b1);

                Book b2 = new Book();
                b2.setTitle("1984");
                b2.setAuthor("George Orwell");
                b2.setIsbn("978-8535909555");
                b2.setPrice(new BigDecimal("39.90"));
                b2.setStock(5);
                bookRepository.save(b2);

                Book b3 = new Book();
                b3.setTitle("Dom Casmurro");
                b3.setAuthor("Machado de Assis");
                b3.setIsbn("978-8508138296");
                b3.setPrice(new BigDecimal("29.90"));
                b3.setStock(8);
                bookRepository.save(b3);

                Book b4 = new Book();
                b4.setTitle("O Pequeno Príncipe");
                b4.setAuthor("Antoine de Saint-Exupéry");
                b4.setIsbn("978-8596064637");
                b4.setPrice(new BigDecimal("24.90"));
                b4.setStock(0);
                bookRepository.save(b4);
                System.out.println("Livros de teste inseridos no Monólito.");
            }

            System.out.println("=== Monólito LibriFlow conectado ao PostgreSQL! ===");
        };
    }
}