package com.example.mockodsvue.purchase.model.entity;

import com.example.mockodsvue.shared.model.AuditEntity;
import com.example.mockodsvue.purchase.model.enums.FrozenStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 營業所凍結單 (BPF)
 * 用於控制營業所某日期的訂單編輯權限
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "branch_purchase_frozen", uniqueConstraints = @UniqueConstraint(columnNames = {"branch_code", "purchase_date"}))
public class BranchPurchaseFrozen extends AuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Integer id;

    @NotNull
    @Size(max = 20)
    @Column(name = "branch_code", length = 20, nullable = false)
    private String branchCode;

    @NotNull
    @Column(name = "purchase_date", nullable = false)
    private LocalDate purchaseDate;

    /**
     * 狀態：FROZEN (已凍結) / CONFIRMED (已確認)
     */
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private FrozenStatus status;

    /**
     * 凍結時間
     */
    @Column(nullable = false)
    private LocalDateTime frozenAt;

    /**
     * 凍結人員工編
     */
    @Size(max = 20)
    @Column(length = 20, nullable = false)
    private String frozenBy;

    /**
     * 確認時間
     */
    @Column
    private LocalDateTime confirmedAt;

    /**
     * 確認人員工編
     */
    @Size(max = 20)
    @Column(length = 20)
    private String confirmedBy;

    // --- 靜態工廠方法 ---

    public static BranchPurchaseFrozen createFrozen(String branchCode, LocalDate purchaseDate, String frozenBy) {
        BranchPurchaseFrozen bpf = new BranchPurchaseFrozen();
        bpf.setBranchCode(branchCode);
        bpf.setPurchaseDate(purchaseDate);
        bpf.setStatus(FrozenStatus.FROZEN);
        bpf.setFrozenAt(LocalDateTime.now());
        bpf.setFrozenBy(frozenBy);
        return bpf;
    }

    // --- 狀態查詢 ---

    public boolean isFrozen() {
        return status == FrozenStatus.FROZEN;
    }

    public boolean isConfirmed() {
        return status == FrozenStatus.CONFIRMED;
    }

    // --- 狀態轉換 ---

    public void confirm(String confirmedBy) {
        if (isConfirmed()) {
            throw new IllegalStateException("營業所已經確認");
        }
        this.status = FrozenStatus.CONFIRMED;
        this.confirmedAt = LocalDateTime.now();
        this.confirmedBy = confirmedBy;
    }

    // --- 前置條件斷言 ---

    public void assertCanUnfreeze() {
        if (isConfirmed()) {
            throw new IllegalStateException("營業所已確認，無法解除凍結");
        }
    }

    public void assertCanEditConfirmedQty() {
        if (status != FrozenStatus.FROZEN) {
            throw new IllegalStateException("營業所已確認，無法再調整確認數量");
        }
    }

    public void assertCanAggregate() {
        if (!isConfirmed()) {
            throw new IllegalStateException("營業所尚未確認，無法彙總");
        }
    }
}
