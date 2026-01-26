package com.example.mockodsvue.model.entity.master;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "product_factory", uniqueConstraints = @UniqueConstraint(columnNames = {"product_code", "factory_code"}))
public class ProductFactory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull
    @Size(max = 20)
    @Column(length = 20, nullable = false)
    private String productCode;

    @NotNull
    @Size(max = 20)
    @Column(length = 20, nullable = false)
    private String factoryCode;

    @NotNull
    @Column(nullable = false, columnDefinition = "boolean default false")
    private Boolean isDefault;
}
