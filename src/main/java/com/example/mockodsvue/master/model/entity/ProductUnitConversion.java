package com.example.mockodsvue.master.model.entity;

import com.example.mockodsvue.shared.model.AuditEntity;
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
@Table(name = "product_unit_conversion", uniqueConstraints = @UniqueConstraint(columnNames = {"product_code", "from_unit", "to_unit"}))
public class ProductUnitConversion extends AuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull
    @Size(max = 20)
    @Column(length = 20, nullable = false)
    private String productCode;

    @NotNull
    @Size(max = 10)
    @Column(length = 10, nullable = false)
    private String fromUnit;

    @NotNull
    @Size(max = 10)
    @Column(length = 10, nullable = false)
    private String toUnit;

    @NotNull
    @Column(precision = 10, scale = 4, nullable = false)
    private BigDecimal conversionRate;
}
