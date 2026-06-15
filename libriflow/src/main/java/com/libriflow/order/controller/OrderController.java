package com.libriflow.order.controller;

import com.libriflow.order.OrderResponseDTO;
import com.libriflow.order.entity.Order;
import com.libriflow.order.service.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    public ResponseEntity<List<Order>> findAll() {
        return ResponseEntity.ok(orderService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Order> findById(@PathVariable Long id) {
        return ResponseEntity.of(orderService.findById(id));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<OrderResponseDTO>> findByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(orderService.findByUserIdWithDetails(userId));
    }

    @GetMapping("/user/{userId}/status/{status}")
    public ResponseEntity<List<Order>> findByUserIdAndStatus(@PathVariable Long userId, @PathVariable String status) {
        return ResponseEntity.ok(orderService.findByUserIdAndStatus(userId, status));
    }

    @PostMapping("/purchase/{userId}")
    public ResponseEntity<OrderResponseDTO> purchase(@PathVariable Long userId,
                                                     @RequestBody List<Long> bookIds) {
        return ResponseEntity.status(HttpStatus.CREATED).body(orderService.purchase(userId, bookIds));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        orderService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
