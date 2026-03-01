package com.orderhub.orders.appication.service;

import aj.org.objectweb.asm.commons.TryCatchBlockSorter;
import com.orderhub.orders.domain.exception.BadRquestException;
import com.orderhub.orders.domain.exception.BusinessRuleException;
import com.orderhub.orders.domain.exception.ResourceNotFoundException;
import com.orderhub.orders.domain.model.Order;
import com.orderhub.orders.domain.model.OrderStatus;
import com.orderhub.orders.infrastructure.persistence.entity.OrderEntity;
import com.orderhub.orders.infrastructure.persistence.repository.OrderJpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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

    @Transactional
    public Order cancel(UUID id) {
        OrderEntity entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found: " + id));

        if (entity.getStatus() == OrderStatus.CANCELLED) {
            throw new BusinessRuleException("Order already cancelled: " + id);
        }

        entity.setStatus(OrderStatus.CANCELLED);

        OrderEntity saved = repository.save(entity);

        return new Order(
                saved.getId(),
                saved.getCustomerId(),
                saved.getTotalAmount(),
                saved.getStatus(),
                saved.getCreatedAt()
        );
    }

    @Transactional(readOnly = true)
    public Page<Order> list(Integer page, Integer size, String status) {

        int pageNumber = (page == null) ? 0 : page;
        int pageSize = (size == null) ? 10 : size;

        Pageable pageable = PageRequest.of(pageNumber, pageSize);

        Page<OrderEntity> result;

        if (status == null || status.isBlank()) {
            result = repository.findAll(pageable);
        } else {
            try {
                OrderStatus parsed = OrderStatus.valueOf(status.trim().toUpperCase());
                result = repository.findAllByStatus(parsed, pageable);
            } catch (IllegalArgumentException ex) {
                throw new BadRquestException("Invalid status value: " + status);
            }
        }

        return result.map(e -> new Order(
                e.getId(),
                e.getCustomerId(),
                e.getTotalAmount(),
                e.getStatus(),
                e.getCreatedAt()
        ));
    }
}
