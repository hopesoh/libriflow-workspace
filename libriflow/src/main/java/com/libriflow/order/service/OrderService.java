package com.libriflow.order.service;

import com.libriflow.order.OrderResponseDTO;
import com.libriflow.order.entity.Order;
import com.libriflow.order.exception.BookNotFoundException;
import com.libriflow.order.exception.BookOutOfStockException;
import com.libriflow.order.exception.InvalidPurchaseRequestException;
import com.libriflow.order.exception.OrderNotFoundException;
import com.libriflow.order.exception.UserNotFoundException;
import com.libriflow.order.integration.book.BookApi;
import com.libriflow.order.integration.book.BookDetailsDTO;
import com.libriflow.order.integration.user.UserApi;
import com.libriflow.order.integration.user.UserDetailsDTO;
import com.libriflow.order.repository.OrderRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

        var purchase = bookIds.stream()
                .map(this::loadAvailableBook)
                .collect(Collectors.teeing(
                        Collectors.toList(),
                        Collectors.reducing(
                                BigDecimal.ZERO,
                                BookDetailsDTO::getPrice,
                                BigDecimal::add
                        ),
                        PurchaseData::new
                ));

        purchase.books().forEach(this::decrementAndUpdateStock);

        Order order = new Order();
        order.setUserId(userId);
        order.setBookIds(purchase.books().stream().map(BookDetailsDTO::getId).toList());
        order.setTotal(purchase.total());
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
            throw new OrderNotFoundException(id);
        }

        orderRepository.deleteById(id);
    }

    private void ensureUserExists(Long userId) {
        if (!userApi.checkUserExists(userId)) {
            throw new UserNotFoundException(userId);
        }
    }

    private void ensureValidBookList(List<Long> bookIds) {
        if (bookIds == null || bookIds.isEmpty()) {
            throw new InvalidPurchaseRequestException("A lista de livros não pode ser vazia.");
        }
    }

    private BookDetailsDTO loadAvailableBook(Long bookId) {
        if (!bookApi.checkBookExists(bookId)) {
            throw new BookNotFoundException(bookId);
        }

        BookDetailsDTO book = bookApi.getBookDetails(bookId);
        if (book.getStock() == null || book.getStock() <= 0) {
            throw new BookOutOfStockException(bookId, book.getTitle());
        }

        return book;
    }

    private void decrementAndUpdateStock(BookDetailsDTO book) {
        book.setStock(book.getStock() - 1);
        bookApi.update(book.getId(), book);
    }

    private record PurchaseData(List<BookDetailsDTO> books, BigDecimal total) {
    }
}
