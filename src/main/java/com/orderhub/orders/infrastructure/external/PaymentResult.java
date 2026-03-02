package com.orderhub.orders.infrastructure.external;

import java.util.UUID;

public record PaymentResult(
        UUID orderId,
        String status, // SUCCESS, PENDING, FAILED
        String transactionId
) {}
