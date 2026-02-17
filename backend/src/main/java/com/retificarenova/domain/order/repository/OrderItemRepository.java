package com.retificarenova.domain.order.repository;

import com.retificarenova.domain.order.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repositório de Itens de Pedido
 */
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
}
