package com.example.mockodsvue.model.entity.delivery;

import com.example.mockodsvue.model.enums.PaymentStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "account_receivable")
public class AccountReceivable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull
    @Size(max = 30)
    @Column(unique = true, length = 30, nullable = false)
    private String arNo;

    @NotNull
    @Size(max = 30)
    @Column(length = 30, nullable = false)
    private String deliveryNo;

    @NotNull
    @Size(max = 20)
    @Column(length = 20, nullable = false)
    private String branchCode;

    @NotNull
    @Size(max = 20)
    @Column(length = 20, nullable = false)
    private String customerCode;

    @NotNull
    @Column(nullable = false)
    private LocalDate arDate;

    @NotNull
    @Column(precision = 12, scale = 2, nullable = false)
    private BigDecimal totalAmount;

    @NotNull
    @Column(precision = 12, scale = 2, nullable = false, columnDefinition = "decimal(12,2) default 0")
    private BigDecimal paidAmount = BigDecimal.ZERO;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private PaymentStatus status;

    @NotNull
    @Column(nullable = false)
    private LocalDate dueDate;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
