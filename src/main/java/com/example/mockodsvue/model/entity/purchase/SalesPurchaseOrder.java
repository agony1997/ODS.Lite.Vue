package com.example.mockodsvue.model.entity.purchase;

import com.example.mockodsvue.model.entity.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;

/**
 * 業務員訂貨單 (SPO)
 * 注意：SPO 本身無狀態欄位，編輯權限由 BPF 控制，彙總狀態由 SPOD 追蹤
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "sales_purchase_order")
public class SalesPurchaseOrder extends BaseEntity {

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
}
