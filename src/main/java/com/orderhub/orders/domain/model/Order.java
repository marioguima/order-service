package com.orderhub.orders.domain.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public class Order {

    private UUID id;
    private String customerId;
    private BigDecimal totalAmount;
    private OrderStatus status;
    private Instant createdAt;

    public Order(UUID id, String customerId, BigDecimal totalAmount, OrderStatus status, Instant createdAt) {
        this.id = id;
        this.customerId = customerId;
        this.totalAmount = totalAmount;
        this.status = status;
        this.createdAt = createdAt;
    }

     public static Order createNew(String customerId, BigDecimal totalAmount) {
        return new Order(
                UUID.randomUUID(),
                customerId,
                totalAmount,
                OrderStatus.CREATED,
                Instant.now()
        );
     }

     public UUID getId() {
        return id;
     }

     public String getCustomerId() {
        return customerId;
     }

     public BigDecimal getTotalAmount() {
        return totalAmount;
     }

     public OrderStatus getStatus() {
        return status;
     }

     public Instant getCreatedAt() {
        return createdAt;
     }
}
