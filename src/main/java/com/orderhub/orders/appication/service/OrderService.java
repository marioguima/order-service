package com.orderhub.orders.appication.service;

import com.orderhub.orders.domain.exception.ResourceNotFoundException;
import com.orderhub.orders.domain.model.Order;
import com.orderhub.orders.infrastructure.persistence.entity.OrderEntity;
import com.orderhub.orders.infrastructure.persistence.repository.OrderJpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class OrderService {

    private final OrderJpaRepository repository;

    public OrderService(OrderJpaRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public Order create(Order order) {

        OrderEntity entity = new OrderEntity(
                order.getId(),
                order.getCustomerId(),
                order.getTotalAmount(),
                order.getStatus(),
                order.getCreatedAt()
        );

        OrderEntity saved = repository.save(entity);

        return  new Order(
                saved.getId(),
                saved.getCustomerId(),
                saved.getTotalAmount(),
                saved.getStatus(),
                saved.getCreatedAt()
        );
    }

    @Transactional(readOnly = true)
    public Order getById(UUID id) {
        OrderEntity entity = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Order not found: " + id));
        if (entity == null) return null;

        return new Order(
                entity.getId(),
                entity.getCustomerId(),
                entity.getTotalAmount(),
                entity.getStatus(),
                entity.getCreatedAt()
        );
    }
}
