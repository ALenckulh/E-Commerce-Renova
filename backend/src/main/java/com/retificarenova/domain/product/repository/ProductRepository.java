package com.retificarenova.domain.product.repository;

import com.retificarenova.domain.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repositório de Produtos
 */
public interface ProductRepository extends JpaRepository<Product, Long> {
}
