package com.example.mockodsvue.allocation.model.entity;

import com.example.mockodsvue.shared.model.AuditEntity;
import com.example.mockodsvue.allocation.model.enums.AllocationStatus;
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
@Table(name = "sales_receive_order")
public class SalesReceiveOrder extends AuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull
    @Size(max = 30)
    @Column(unique = true, length = 30, nullable = false)
    private String receiveNo;

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
    private LocalDate receiveDate;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private AllocationStatus status;

    private LocalDateTime receivedAt;
}
