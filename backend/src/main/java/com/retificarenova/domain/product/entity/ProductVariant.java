package com.retificarenova.domain.product.entity;

import com.retificarenova.util.MoneyUtils;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "product_variants")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductVariant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false, unique = true, length = 100)
    private String sku;

    @Column(length = 50)
    private String size;

    @Column(length = 50)
    private String color;

    @Column(name = "stock_quantity", nullable = false)
    private Integer stockQuantity = 0;

    @Column(precision = 10, scale = 2)
    private BigDecimal price;

    @Column(precision = 10, scale = 3)
    private BigDecimal weight;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = createdAt;
        normalizePrice();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
        normalizePrice();
    }

    /**
     * Valida e normaliza o preço da variante do produto
     * Se não definido, herda o preço do produto pai
     */
    private void normalizePrice() {
        if (this.price != null) {
            if (!MoneyUtils.isValidMoney(this.price)) {
                throw new IllegalArgumentException(
                    "Preço da variante inválido: " + this.price + 
                    ". Deve ser positivo e ter no máximo 2 casas decimais"
                );
            }
            this.price = MoneyUtils.roundMoney(this.price);
        } else if (this.product != null && this.product.getPrice() != null) {
            // Se a variante não tem preço, usa o preço do produto pai
            this.price = MoneyUtils.roundMoney(this.product.getPrice());
        }
    }
}

