package com.example.mockodsvue.master.model.entity;

import com.example.mockodsvue.shared.model.AuditEntity;
import com.example.mockodsvue.master.model.enums.ProductCategory;
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
@Table(name = "product")
public class Product extends AuditEntity {

    @Id
    @Size(max = 20)
    @Column(length = 20)
    private String productCode;

    @NotNull
    @Size(max = 100)
    @Column(length = 100, nullable = false)
    private String productName;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private ProductCategory category;

    @NotNull
    @Size(max = 10)
    @Column(length = 10, nullable = false)
    private String baseUnit;

    @NotNull
    @Column(precision = 10, scale = 2, nullable = false)
    private BigDecimal basePrice;

    @NotNull
    @Size(max = 20)
    @Column(length = 20, nullable = false)
    private String status;
}
