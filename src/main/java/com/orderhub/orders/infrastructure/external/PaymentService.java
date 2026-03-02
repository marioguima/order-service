package com.orderhub.orders.infrastructure.external;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Random;
import java.util.UUID;

@Service
public class PaymentService {

    private static final Logger log = LoggerFactory.getLogger(PaymentService.class);
    private final Random random = new Random();

    @Value("${app.payment.failure-rate:0.5}")
    private double failureRate;

    @CircuitBreaker(name = "paymentService", fallbackMethod = "processPaymentFallback")
    @Retry(name = "paymentService")
    public PaymentResult processPayment(UUID orderId, BigDecimal amount) {
        log.info("Processing payment for order {} amount {}", orderId, amount);

        simulateLatency();

        if (Math.random() < failureRate) {
            log.error("Payment service failed");
            throw new RuntimeException("Payment service unavailable");
        }

        log.info("Payment successful for order {}", orderId);
        return new PaymentResult(orderId,
                "SUCCESS",
                "PAY-" + UUID.randomUUID().toString().substring(0, 8));
    }

    private void simulateLatency() {
        try {
            Thread.sleep(random.nextInt(500) + 100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
