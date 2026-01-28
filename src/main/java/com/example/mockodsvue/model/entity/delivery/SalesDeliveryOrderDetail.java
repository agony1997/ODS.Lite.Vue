package com.example.mockodsvue.model.entity.delivery;

import com.example.mockodsvue.model.entity.BaseEntity;
import com.example.mockodsvue.model.enums.DeliveryDetailType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "sales_delivery_order_detail")
public class SalesDeliveryOrderDetail extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull
    @Size(max = 30)
    @Column(length = 30, nullable = false)
    private String deliveryNo;

    @NotNull
    @Column(nullable = false)
    private Integer itemNo;

    @Size(max = 30)
    @Column(length = 30)
    private String preOrderNo;

    @NotNull
    @Size(max = 20)
    @Column(length = 20, nullable = false)
    private String productCode;

    @NotNull
    @Size(max = 30)
    @Column(length = 30, nullable = false)
    private String batchNo;

    @NotNull
    @Column(nullable = false)
    private LocalDate expiryDate;

    @NotNull
    @Column(nullable = false)
    private Integer qty;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private DeliveryDetailType type;

    @NotNull
    @Column(precision = 10, scale = 2, nullable = false)
    private BigDecimal unitPrice;

    @NotNull
    @Column(precision = 12, scale = 2, nullable = false)
    private BigDecimal amount;

    @NotNull
    @Size(max = 10)
    @Column(length = 10, nullable = false)
    private String unit;
}
