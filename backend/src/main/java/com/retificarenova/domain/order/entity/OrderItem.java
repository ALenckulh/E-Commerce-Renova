package com.retificarenova.domain.order.entity;

import com.retificarenova.domain.product.entity.Product;
import com.retificarenova.domain.product.entity.ProductVariant;
import com.retificarenova.util.MoneyUtils;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "order_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_variant_id")
    private ProductVariant productVariant;

    @Column(nullable = false)
    private Integer quantity;

    @Column(name = "unit_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal unitPrice;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal subtotal;

    @PrePersist
    protected void onCreate() {
        normalizeAndCalculateMoney();
    }

    @PreUpdate
    protected void onUpdate() {
        normalizeAndCalculateMoney();
    }

    /**
     * Normaliza os valores monetários e recalcula o subtotal
     * Garante que unitPrice e subtotal sejam válidos e consistentes
     */
    private void normalizeAndCalculateMoney() {
        // Normaliza o preço unitário
        if (this.unitPrice != null) {
            if (!MoneyUtils.isValidMoney(this.unitPrice)) {
                throw new IllegalArgumentException(
                    "Preço unitário inválido: " + this.unitPrice + 
                    ". Deve ser positivo e ter no máximo 2 casas decimais"
                );
            }
            this.unitPrice = MoneyUtils.roundMoney(this.unitPrice);
        }

        // Calcula ou normaliza o subtotal (quantidade × preço unitário)
        if (this.unitPrice != null && this.quantity != null && this.quantity > 0) {
            BigDecimal calculatedSubtotal = this.unitPrice
                .multiply(BigDecimal.valueOf(this.quantity));
            this.subtotal = MoneyUtils.roundMoney(calculatedSubtotal);
        } else if (this.subtotal != null) {
            // Se não conseguir calcular, apenas normaliza o subtotal existente
            this.subtotal = MoneyUtils.roundMoney(this.subtotal);
        }
    }
}
