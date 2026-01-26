package com.example.mockodsvue.model.entity.purchase;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 業務員訂貨單 (SPO)
 * 注意：SPO 本身無狀態欄位，編輯權限由 BPF 控制，彙總狀態由 SPOD 追蹤
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "sales_purchase_order")
public class SalesPurchaseOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Integer id;

    @NotNull
    @Size(max = 30)
    @Column(length = 30, nullable = false, unique = true)
    private String purchaseNo;

    @NotNull
    @Size(max = 20)
    @Column(length = 20, nullable = false)
    private String branchCode;

    @NotNull
    @Size(max = 20)
    @Column(length = 20, nullable = false)
    private String locationCode;

    @NotNull
    @Column(nullable = false)
    private LocalDate purchaseDate;

    @NotNull
    @Size(max = 20)
    @Column(length = 20, nullable = false)
    private String purchaseUser;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
