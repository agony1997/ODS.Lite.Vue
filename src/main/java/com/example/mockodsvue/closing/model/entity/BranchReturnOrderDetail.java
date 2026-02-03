package com.example.mockodsvue.closing.model.entity;

import com.example.mockodsvue.shared.model.AuditEntity;
import com.example.mockodsvue.closing.model.enums.ReturnReason;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "branch_return_order_detail")
public class BranchReturnOrderDetail extends AuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull
    @Size(max = 30)
    @Column(length = 30, nullable = false)
    private String broNo;

    @NotNull
    @Column(nullable = false)
    private Integer itemNo;

    @NotNull
    @Size(max = 30)
    @Column(length = 30, nullable = false)
    private String returnNo;

    @NotNull
    @Size(max = 20)
    @Column(length = 20, nullable = false)
    private String productCode;

    @NotNull
    @Size(max = 30)
    @Column(length = 30, nullable = false)
    private String batchNo;

    @NotNull
    @Column(nullable = false)
    private LocalDate expiryDate;

    @NotNull
    @Column(nullable = false)
    private Integer qty;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private ReturnReason reason;

    @NotNull
    @Size(max = 10)
    @Column(length = 10, nullable = false)
    private String unit;
}
