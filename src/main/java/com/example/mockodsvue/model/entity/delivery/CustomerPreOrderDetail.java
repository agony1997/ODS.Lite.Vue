package com.example.mockodsvue.model.entity.delivery;

import com.example.mockodsvue.model.entity.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "customer_pre_order_detail")
public class CustomerPreOrderDetail extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull
    @Size(max = 30)
    @Column(length = 30, nullable = false)
    private String preOrderNo;

    @NotNull
    @Column(nullable = false)
    private Integer itemNo;

    @NotNull
    @Size(max = 20)
    @Column(length = 20, nullable = false)
    private String productCode;

    @NotNull
    @Size(max = 100)
    @Column(length = 100, nullable = false)
    private String productName;

    @NotNull
    @Size(max = 10)
    @Column(length = 10, nullable = false)
    private String unit;

    @NotNull
    @Column(nullable = false)
    private Integer qty;

    @NotNull
    @Column(precision = 10, scale = 2, nullable = false)
    private BigDecimal unitPrice;
}
