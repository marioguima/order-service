package com.orderhub.orders.appication.service;

import com.orderhub.orders.domain.exception.BusinessRuleException;
import com.orderhub.orders.domain.exception.ResourceNotFoundException;
import com.orderhub.orders.domain.model.Order;
import com.orderhub.orders.domain.model.OrderStatus;
import com.orderhub.orders.infrastructure.external.PaymentService;
import com.orderhub.orders.infrastructure.messaging.KafkaProducerService;
import com.orderhub.orders.infrastructure.metrics.OrderMetrics;
import com.orderhub.orders.infrastructure.persistence.entity.OrderEntity;
import com.orderhub.orders.infrastructure.persistence.repository.OrderJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OrderServiceTest {

    private OrderJpaRepository repository;
    private OrderMetrics metrics;
    private OrderService service;
    private KafkaProducerService kafkaProducerService;
    private PaymentService paymentService;

    @BeforeEach
    void setup() {
        repository = mock(OrderJpaRepository.class);
        metrics = mock(OrderMetrics.class);
        kafkaProducerService = mock(KafkaProducerService.class);
        paymentService = mock(PaymentService.class);
        service = new OrderService(repository, metrics, kafkaProducerService, paymentService);
    }

    @Test
    void cancel_shouldThrowNotFound_whenOrderDoesNotExist() {
        UUID id = UUID.randomUUID();
        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.cancel(id));
        verify(repository, never()).save(any());
        verify(metrics, never()).incrementCancelled();
    }

    @Test
    void cancel_shouldThrowBusinessRule_whenAlreadyCancelled() {
        UUID id = UUID.randomUUID();

        OrderEntity entity = new OrderEntity(
                id,
                "c-123",
                new BigDecimal("10.00"),
                OrderStatus.CANCELLED,
                Instant.now());

        when(repository.findById(id)).thenReturn(Optional.of(entity));

        assertThrows(BusinessRuleException.class, () -> service.cancel(id));
        verify(repository, never()).save(any());
        verify(metrics, never()).incrementCancelled();
    }

    @Test
    void cancel_shouldSetStatusCancelled_andIncrementMetric() {
        UUID id = UUID.randomUUID();

        OrderEntity entity = new OrderEntity(
                id,
                "c-123",
                new BigDecimal("10.00"),
                OrderStatus.CREATED,
                Instant.now());

        when(repository.findById(id)).thenReturn(Optional.of(entity));
        when(repository.save(any(OrderEntity.class))).thenAnswer(inv -> inv.getArgument(0));

        Order result = service.cancel(id);

        assertEquals(OrderStatus.CANCELLED, result.getStatus());
        verify(repository, times(1)).save(any(OrderEntity.class));
        verify(metrics, times(1)).incrementCancelled();
    }
}