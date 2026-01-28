package com.example.mockodsvue.model.entity.delivery;

import com.example.mockodsvue.model.entity.BaseEntity;
import com.example.mockodsvue.model.enums.DeliveryOrderStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "sales_delivery_order")
public class SalesDeliveryOrder extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull
    @Size(max = 30)
    @Column(unique = true, length = 30, nullable = false)
    private String deliveryNo;

    @NotNull
    @Size(max = 20)
    @Column(length = 20, nullable = false)
    private String branchCode;

    @NotNull
    @Size(max = 20)
    @Column(length = 20, nullable = false)
    private String locationCode;

    @NotNull
    @Size(max = 20)
    @Column(length = 20, nullable = false)
    private String customerCode;

    @NotNull
    @Column(nullable = false)
    private LocalDate deliveryDate;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private DeliveryOrderStatus status;

    private LocalDateTime deliveredAt;

    private LocalDateTime signedAt;

    @Size(max = 50)
    @Column(length = 50)
    private String signedBy;

    @Column(precision = 12, scale = 2)
    private BigDecimal totalAmount;
}
