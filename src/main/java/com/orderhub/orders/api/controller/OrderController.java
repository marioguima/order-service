package com.orderhub.orders.api.controller;

import com.orderhub.orders.api.dto.CreateOrderRequest;
import com.orderhub.orders.api.dto.OrderResponse;
import com.orderhub.orders.appication.service.OrderService;
import com.orderhub.orders.domain.model.Order;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService service;

    public OrderController(OrderService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<OrderResponse> create(@Valid @RequestBody CreateOrderRequest request) {

        Order created = service.create(
                Order.createNew(request.getCustomerId(), request.getTotalAmount())
        );

        OrderResponse response = new OrderResponse(
                created.getId(),
                created.getCustomerId(),
                created.getTotalAmount(),
                created.getStatus(),
                created.getCreatedAt()
        );

        return ResponseEntity
                .created(URI.create("/orders/" + created.getId()))
                .body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getById(@PathVariable UUID id) {

        Order found = service.getById(id);

        if (found == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(
                new OrderResponse(
                        found.getId(),
                        found.getCustomerId(),
                        found.getTotalAmount(),
                        found.getStatus(),
                        found.getCreatedAt()
                )
        );
    }
}
