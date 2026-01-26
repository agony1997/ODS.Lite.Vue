package com.example.mockodsvue.model.entity.purchase;

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
@Table(name = "branch_purchase_order_detail", uniqueConstraints = @UniqueConstraint(columnNames = {"bpo_no", "product_code", "unit"}))
public class BranchPurchaseOrderDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull
    @Size(max = 30)
    @Column(length = 30, nullable = false)
    private String bpoNo;

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
}
