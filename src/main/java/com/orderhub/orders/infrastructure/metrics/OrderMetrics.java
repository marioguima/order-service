package com.orderhub.orders.infrastructure.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;

@Component
public class OrderMetrics {

    private final Counter ordersCreated;
    private final Counter ordersCancelled;

    public OrderMetrics(MeterRegistry registry) {
        this.ordersCreated = Counter.builder("orders_created_total")
                .description("Total number of created orders")
                .register(registry);
        this.ordersCancelled = Counter.builder("orders_cancelled_total")
                .description("Total number of cancelled orders")
                .register(registry);
    }

    public void incrementCreated() {
        ordersCreated.increment();
    }

    public void incrementCancelled() {
        ordersCancelled.increment();
    }
}
