package com.example.mockodsvue.closing.model.entity;

import com.example.mockodsvue.shared.model.AuditEntity;
import com.example.mockodsvue.closing.model.enums.BranchReturnStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "branch_return_order")
public class BranchReturnOrder extends AuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull
    @Size(max = 30)
    @Column(unique = true, length = 30, nullable = false)
    private String broNo;

    @NotNull
    @Size(max = 20)
    @Column(length = 20, nullable = false)
    private String branchCode;

    @NotNull
    @Size(max = 20)
    @Column(length = 20, nullable = false)
    private String factoryCode;

    @NotNull
    @Column(nullable = false)
    private LocalDate returnDate;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private BranchReturnStatus status;

    private LocalDateTime returnedAt;
}
