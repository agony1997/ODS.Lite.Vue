package com.example.mockodsvue.purchase.model.entity;

import com.example.mockodsvue.shared.model.AuditEntity;
import com.example.mockodsvue.purchase.model.enums.SalesOrderDetailStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

/**
 * 業務員訂貨單明細 (SPOD)
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "sales_purchase_order_detail", uniqueConstraints = @UniqueConstraint(columnNames = {"purchase_no", "product_code", "unit"}))
public class SalesPurchaseOrderDetail extends AuditEntity {

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

    // --- 狀態查詢 ---

    public boolean isPending() {
        return status == SalesOrderDetailStatus.PENDING;
    }

    public boolean isAggregated() {
        return status == SalesOrderDetailStatus.AGGREGATED;
    }

    // --- 狀態轉換 ---

    public void markAsAggregated() {
        if (isAggregated()) {
            throw new IllegalStateException("此明細已彙總，不可重複彙總");
        }
        this.status = SalesOrderDetailStatus.AGGREGATED;
    }

    // --- 業務行為 ---

    public void initializeConfirmedQty() {
        this.confirmedQty = this.qty;
    }
}
