package com.libriflow.order.controller;

import com.libriflow.order.OrderResponseDTO;
import com.libriflow.order.entity.Order;
import com.libriflow.order.exception.OrderNotFoundException;
import com.libriflow.order.service.OrderService;
import jakarta.validation.constraints.Positive;
import org.springframework.validation.annotation.Validated;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Validated
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
    public ResponseEntity<Order> findById(@PathVariable @Positive Long id) {
        return orderService.findById(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new OrderNotFoundException(id));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<OrderResponseDTO>> findByUserId(@PathVariable @Positive Long userId) {
        return ResponseEntity.ok(orderService.findByUserIdWithDetails(userId));
    }

    @GetMapping("/user/{userId}/status/{status}")
    public ResponseEntity<List<Order>> findByUserIdAndStatus(@PathVariable @Positive Long userId, @PathVariable String status) {
        return ResponseEntity.ok(orderService.findByUserIdAndStatus(userId, status));
    }

    @PostMapping("/purchase/{userId}")
    public ResponseEntity<OrderResponseDTO> purchase(@PathVariable @Positive Long userId,
                                                     @RequestBody List<Long> bookIds) {
        return ResponseEntity.status(HttpStatus.CREATED).body(orderService.purchase(userId, bookIds));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable @Positive Long id) {
        orderService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
