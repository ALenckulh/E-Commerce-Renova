package com.retificarenova.domain.order.repository;

import com.retificarenova.domain.order.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repositório de Pedidos
 */
public interface OrderRepository extends JpaRepository<Order, Long> {
}
