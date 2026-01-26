package com.example.mockodsvue.model.entity.purchase;

import com.example.mockodsvue.model.enums.FrozenStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 營業所凍結單 (BPF)
 * 用於控制營業所某日期的訂單編輯權限
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "branch_purchase_frozen", uniqueConstraints = @UniqueConstraint(columnNames = {"branch_code", "purchase_date"}))
public class BranchPurchaseFrozen {

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
}
