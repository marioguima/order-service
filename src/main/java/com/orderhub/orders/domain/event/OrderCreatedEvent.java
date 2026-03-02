package com.orderhub.orders.domain.event;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record OrderCreatedEvent(
        UUID orderId,
        String customerName,
        BigDecimal totalAmount,
        Instant creatAt
) {}
