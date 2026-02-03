package com.example.mockodsvue.purchase.model.entity;

import com.example.mockodsvue.shared.model.AuditEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "sales_purchase_list", uniqueConstraints = @UniqueConstraint(columnNames = {"location_code", "product_code", "unit"}))
public class SalesPurchaseList extends AuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Integer id;

    @NotNull
    @Size(max = 20)
    @Column(length = 20, nullable = false)
    private String locationCode;

    @NotNull
    @Size(max = 20)
    @Column(nullable = false, length = 20)
    private String productCode;

    @NotNull
    @Size(max = 5)
    @Column(nullable = false, length = 5)
    private String unit;

    @NotNull
    @Column(nullable = false, columnDefinition = "integer default 0")
    private int qty;

    @NotNull
    @Column(nullable = false, columnDefinition = "integer default 0")
    private int sortOrder;
}
