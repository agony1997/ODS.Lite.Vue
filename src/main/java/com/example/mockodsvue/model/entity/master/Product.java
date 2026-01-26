package com.example.mockodsvue.model.entity.master;

import com.example.mockodsvue.model.enums.ProductCategory;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "product")
public class Product {

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
