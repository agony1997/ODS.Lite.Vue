package com.example.mockodsvue.model.entity.purchase;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Table(name = "sales_purchase_order_detail", uniqueConstraints = @UniqueConstraint(columnNames = {"purchase_no", "product_code", "unit"}))
@Data
@Entity
public class SalesPurchaseOrderDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Integer id;

    @NotNull
    @Size(max = 10)
    @Column(nullable = false, length = 10)
    private String purchaseNo;

    @NotNull
    @Column(nullable = false)
    private int itemNo;

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
    private Integer qty = 0;

    @NotNull
    @Column(nullable = false, columnDefinition = "integer default 0")
    private Integer confirmQty = 0;

}
