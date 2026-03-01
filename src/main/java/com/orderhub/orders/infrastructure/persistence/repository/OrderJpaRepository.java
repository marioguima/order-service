package com.orderhub.orders.infrastructure.persistence.repository;

import com.orderhub.orders.domain.model.OrderStatus;
import com.orderhub.orders.infrastructure.persistence.entity.OrderEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface OrderJpaRepository extends JpaRepository<OrderEntity, UUID> {

    Page<OrderEntity> findAllByStatus(OrderStatus status, Pageable pageable);
}
