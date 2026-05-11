package com.libriflow.controller;

import com.libriflow.model.Book;
import com.libriflow.model.Order;
import com.libriflow.order.OrderResponseDTO;
import com.libriflow.order.integration.UserApi;
import com.libriflow.order.integration.UserDetailsDTO;
import com.libriflow.repository.BookRepository;
import com.libriflow.repository.OrderRepository;
import com.libriflow.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    // Service injetado, mas a lógica principal ignora ele - confusão intencional
    @Autowired
    private OrderService orderService;

    // Repositórios de outros domínios injetados diretamente no controller
    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private BookRepository bookRepository;

    private final UserApi userApi;

    public OrderController(UserApi userApi) {
        this.userApi = userApi;
    }

    @GetMapping
    public List<Order> findAll() {
        return orderService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Order> findById(@PathVariable Long id) {
        return orderService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<?>  findByUserId(@PathVariable Long userId) {

        if (!userApi.checkUserExists(userId))
            return ResponseEntity
                    .badRequest()
                    .body("Usuário não encontrado com id: " + userId);

        UserDetailsDTO userDetails = userApi.getUserDetails(userId);
        return ResponseEntity.ok(orderService
                .findByUserId(userId)
                .stream()
                .map(order -> new OrderResponseDTO(order.getId(), order.getTotal(), userDetails.name(), userDetails.email()))
                .toList()
        );
    }

    @GetMapping("/user/{userId}/status/{status}")
    public List<Order> findByUserIdAndStatus(@PathVariable Long userId, @PathVariable String status) {
        return orderService.findByUserIdAndStatus(userId, status);
    }

    /**
     * Endpoint de compra.
     * ATENÇÃO: Toda a lógica de negócio está aqui no controller propositalmente.
     * Validação de estoque, cálculo de preço, atualização de inventário e criação
     * do pedido foram colocados aqui para dificultar o reúso e a testabilidade.
     */
    @PostMapping("/purchase/{userId}")
    public ResponseEntity<?> purchase(@PathVariable Long userId,
                                      @RequestBody List<Long> bookIds) {

        if (!userApi.checkUserExists(userId))
            return ResponseEntity
                .badRequest()
                .body("Usuário não encontrado com id: " + userId);

        if (bookIds == null || bookIds.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body("A lista de livros não pode ser vazia.");
        }

        List<Book> booksDosPedido = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;

        // --- LÓGICA DE NEGÓCIO NO CONTROLLER: validação de estoque e cálculo de preço ---
        for (Long bookId : bookIds) {
            Optional<Book> bookOpt = bookRepository.findById(bookId);

            if (bookOpt.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body("Livro não encontrado com id: " + bookId);
            }

            Book book = bookOpt.get();

            // Validação de estoque diretamente no controller
            if (book.getStock() == null || book.getStock() <= 0) {
                return ResponseEntity.badRequest()
                        .body("Livro sem estoque: \"" + book.getTitle() + "\" (id=" + bookId + ")");
            }

            // Cálculo de preço total diretamente no controller
            total = total.add(book.getPrice());

            // Decremento de estoque dentro do loop - N+1 writes no banco
            book.setStock(book.getStock() - 1);
            bookRepository.save(book); // salva a cada iteração em vez de usar saveAll

            booksDosPedido.add(book);
        }

        // --- CRIAÇÃO DO PEDIDO NO CONTROLLER ---
        Order order = new Order();
        order.setUserId(userId);
        order.setBooks(booksDosPedido);
        order.setTotal(total);
        order.setCreatedAt(LocalDateTime.now());
        order.setStatus("CONFIRMADO");

        Order saved = orderRepository.save(order);
        UserDetailsDTO userDetails = userApi.getUserDetails(userId);

        return ResponseEntity.ok(new OrderResponseDTO(
                saved.getId(),
                saved.getTotal(),
                userDetails.name(),
                userDetails.email()
        ));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        orderRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
