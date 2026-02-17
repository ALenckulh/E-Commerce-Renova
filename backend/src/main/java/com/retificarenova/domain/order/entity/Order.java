package com.retificarenova.domain.order.entity;

import com.retificarenova.domain.auth.entity.User;
import com.retificarenova.domain.auth.entity.UserAddress;
import com.retificarenova.domain.shared.enums.OrderStatus;
import com.retificarenova.domain.shared.enums.PaymentMethod;
import com.retificarenova.domain.shared.enums.PaymentStatus;
import com.retificarenova.util.MoneyUtils;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "delivery_address_id", nullable = false)
    private UserAddress deliveryAddress;

    @Column(name = "subtotal_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal subtotalAmount;

    @Column(name = "discount_amount", precision = 10, scale = 2)
    private BigDecimal discountAmount = BigDecimal.ZERO;

    @Column(name = "shipping_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal shippingAmount;

    @Column(name = "total_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmount;

    @Column(name = "shipping_service", nullable = false, length = 50)
    private String shippingService;

    @Column(name = "delivery_days", nullable = false)
    private Integer deliveryDays;

    @Column(name = "melhor_envio_service_id")
    private Integer melhorEnvioServiceId;

    @Column(name = "tracking_code", length = 50)
    private String trackingCode;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method", nullable = false, length = 50)
    private PaymentMethod paymentMethod;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status", nullable = false, length = 50)
    private PaymentStatus paymentStatus = PaymentStatus.PENDING;

    @Enumerated(EnumType.STRING)
    @Column(name = "order_status", nullable = false, length = 50)
    private OrderStatus orderStatus = OrderStatus.PENDING;

    @Column(name = "shipped_at")
    private LocalDateTime shippedAt;

    @Column(name = "delivered_at")
    private LocalDateTime deliveredAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = createdAt;
        normalizeMoney();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
        normalizeMoney();
    }

    /**
     * Normaliza todos os valores monetários arredondando para 2 casas decimais
     * usando a estratégia HALF_UP para consistência
     */
    private void normalizeMoney() {
        this.subtotalAmount = MoneyUtils.roundMoney(this.subtotalAmount);
        this.discountAmount = MoneyUtils.roundMoney(this.discountAmount);
        this.shippingAmount = MoneyUtils.roundMoney(this.shippingAmount);
        this.totalAmount = MoneyUtils.roundMoney(this.totalAmount);
    }
}
