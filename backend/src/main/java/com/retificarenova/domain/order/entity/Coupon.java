package com.retificarenova.domain.order.entity;

import com.retificarenova.domain.shared.enums.DiscountType;
import com.retificarenova.util.MoneyUtils;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "coupons")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Coupon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String code;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "discount_type", nullable = false, length = 20)
    private DiscountType discountType;

    @Column(name = "discount_value", nullable = false, precision = 10, scale = 2)
    private BigDecimal discountValue;

    @Column(name = "min_purchase_amount", precision = 10, scale = 2)
    private BigDecimal minPurchaseAmount;

    @Column(name = "max_uses")
    private Integer maxUses;

    @Column(name = "current_uses")
    private Integer currentUses = 0;

    @Column(name = "valid_from", nullable = false)
    private LocalDateTime validFrom;

    @Column(name = "valid_until", nullable = false)
    private LocalDateTime validUntil;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = createdAt;
        validateAndNormalizeMoney();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
        validateAndNormalizeMoney();
    }

    /**
     * Valida e normaliza todos os valores monetários do cupom
     * Garante que discountValue e minPurchaseAmount sejam válidos
     */
    private void validateAndNormalizeMoney() {
        // Valida e normaliza o valor de desconto
        if (this.discountValue != null) {
            if (!MoneyUtils.isValidMoney(this.discountValue)) {
                throw new IllegalArgumentException(
                    "Desconto inválido: " + this.discountValue + 
                    ". Deve ser positivo e ter no máximo 2 casas decimais"
                );
            }
            this.discountValue = MoneyUtils.roundMoney(this.discountValue);
        }

        // Valida e normaliza o valor mínimo de compra (se presente)
        if (this.minPurchaseAmount != null && this.minPurchaseAmount.compareTo(BigDecimal.ZERO) > 0) {
            if (!MoneyUtils.isValidMoney(this.minPurchaseAmount)) {
                throw new IllegalArgumentException(
                    "Valor mínimo de compra inválido: " + this.minPurchaseAmount + 
                    ". Deve ser positivo e ter no máximo 2 casas decimais"
                );
            }
            this.minPurchaseAmount = MoneyUtils.roundMoney(this.minPurchaseAmount);
        }
    }
}

