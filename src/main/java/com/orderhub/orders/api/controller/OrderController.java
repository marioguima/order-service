package com.orderhub.orders.api.controller;

import com.orderhub.orders.api.dto.CreateOrderRequest;
import com.orderhub.orders.api.dto.OrderResponse;
import com.orderhub.orders.api.dto.PageResponse;
import com.orderhub.orders.appication.service.OrderService;
import com.orderhub.orders.domain.model.Order;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.UUID;
import java.util.stream.Collectors;

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

    @PostMapping("/{id}/cancel")
    public ResponseEntity<OrderResponse> cancel(@PathVariable UUID id) {

        Order cancelled = service.cancel(id);

        return ResponseEntity.ok(new OrderResponse(
                cancelled.getId(),
                cancelled.getCustomerId(),
                cancelled.getTotalAmount(),
                cancelled.getStatus(),
                cancelled.getCreatedAt()
        ));
    }

    @GetMapping
    public ResponseEntity<PageResponse<OrderResponse>> list(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) String status
    ) {

        Page<Order> result = service.list(page, size, status);

        var content = result.getContent()
                .stream()
                .map(o -> new OrderResponse(
                        o.getId(),
                        o.getCustomerId(),
                        o.getTotalAmount(),
                        o.getStatus(),
                        o.getCreatedAt()
                ))
                .collect(Collectors.toList());

        PageResponse<OrderResponse> response = new PageResponse<>(
                content,
                result.getNumber(),
                result.getSize(),
                result.getTotalElements(),
                result.getTotalPages()
        );

        return ResponseEntity.ok(response);
    }
}
