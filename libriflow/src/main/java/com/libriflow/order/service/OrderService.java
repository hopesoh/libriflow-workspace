package com.libriflow.order.service;

import com.libriflow.order.OrderResponseDTO;
import com.libriflow.order.entity.Order;
import com.libriflow.order.integration.book.BookApi;
import com.libriflow.order.integration.book.BookDetailsDTO;
import com.libriflow.order.integration.user.UserApi;
import com.libriflow.order.integration.user.UserDetailsDTO;
import com.libriflow.order.repository.OrderRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final BookApi bookApi;
    private final UserApi userApi;

    public OrderService(OrderRepository orderRepository, BookApi bookApi, UserApi userApi) {
        this.orderRepository = orderRepository;
        this.bookApi = bookApi;
        this.userApi = userApi;
    }

    public List<Order> findAll() {
        return orderRepository.findAll();
    }

    public Optional<Order> findById(Long id) {
        return orderRepository.findById(id);
    }

    public List<Order> findByUserId(Long userId) {
        return orderRepository.findByUserId(userId);
    }

    public List<Order> findByUserIdAndStatus(Long userId, String status) {
        return orderRepository.findByUserIdAndStatus(userId, status);
    }

    public List<OrderResponseDTO> findByUserIdWithDetails(Long userId) {
        ensureUserExists(userId);

        UserDetailsDTO userDetails = userApi.getUserDetails(userId);
        return orderRepository.findByUserId(userId)
                .stream()
                .map(order -> new OrderResponseDTO(
                        order.getId(),
                        order.getTotal(),
                        userDetails.name(),
                        userDetails.email()
                ))
                .toList();
    }

    public OrderResponseDTO purchase(Long userId, List<Long> bookIds) {
        ensureUserExists(userId);
        ensureValidBookList(bookIds);

        List<BookDetailsDTO> books = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;

        for (Long bookId : bookIds) {
            BookDetailsDTO book = loadAvailableBook(bookId);
            books.add(book);
            total = total.add(book.getPrice());
        }

        for (BookDetailsDTO book : books) {
            book.setStock(book.getStock() - 1);
            bookApi.update(book.getId(), book);
        }

        Order order = new Order();
        order.setUserId(userId);
        order.setBookIds(books.stream().map(BookDetailsDTO::getId).toList());
        order.setTotal(total);
        order.setCreatedAt(LocalDateTime.now());
        order.setStatus("CONFIRMADO");

        Order saved = orderRepository.save(order);
        UserDetailsDTO userDetails = userApi.getUserDetails(userId);

        return new OrderResponseDTO(
                saved.getId(),
                saved.getTotal(),
                userDetails.name(),
                userDetails.email()
        );
    }

    public void deleteById(Long id) {
        if (!orderRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Pedido não encontrado com id: " + id);
        }

        orderRepository.deleteById(id);
    }

    private void ensureUserExists(Long userId) {
        if (!userApi.checkUserExists(userId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado com id: " + userId);
        }
    }

    private void ensureValidBookList(List<Long> bookIds) {
        if (bookIds == null || bookIds.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A lista de livros não pode ser vazia.");
        }
    }

    private BookDetailsDTO loadAvailableBook(Long bookId) {
        if (!bookApi.checkBookExists(bookId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Livro não encontrado com id: " + bookId);
        }

        BookDetailsDTO book = bookApi.getBookDetails(bookId);
        if (book.getStock() == null || book.getStock() <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Livro sem estoque: \"" + book.getTitle() + "\" (id=" + bookId + ")"
            );
        }

        return book;
    }
}
