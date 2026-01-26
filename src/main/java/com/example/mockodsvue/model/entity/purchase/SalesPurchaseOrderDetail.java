package com.example.mockodsvue.model.entity.purchase;

import com.example.mockodsvue.model.enums.SalesOrderDetailStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 業務員訂貨單明細 (SPOD)
 */
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
    @Size(max = 30)
    @Column(nullable = false, length = 30)
    private String purchaseNo;

    @NotNull
    @Column(nullable = false)
    private int itemNo;

    @NotNull
    @Size(max = 20)
    @Column(nullable = false, length = 20)
    private String productCode;

    @Size(max = 40)
    @Column(length = 40)
    private String productName;

    @NotNull
    @Size(max = 5)
    @Column(nullable = false, length = 5)
    private String unit;

    /**
     * 訂購數量 (業務員填寫)
     */
    @NotNull
    @Column(nullable = false, columnDefinition = "integer default 0")
    private int qty;

    /**
     * 確認數量 (組長調整，預設 = qty)
     */
    @NotNull
    @Column(nullable = false, columnDefinition = "integer default 0")
    private int confirmedQty;

    /**
     * 上次訂購數量
     */
    @Column(columnDefinition = "integer default 0")
    private int lastQty;

    /**
     * 狀態：PENDING (待彙總) / AGGREGATED (已彙總)
     */
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private SalesOrderDetailStatus status = SalesOrderDetailStatus.PENDING;

    /**
     * 排序順序
     */
    @Column(columnDefinition = "integer default 0")
    private int sortOrder;
}
