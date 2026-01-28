package com.example.mockodsvue.model.entity.allocation;

import com.example.mockodsvue.model.entity.BaseEntity;
import com.example.mockodsvue.model.enums.AllocationStatus;
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
@Table(name = "allocation_order_detail")
public class AllocationOrderDetail extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull
    @Size(max = 30)
    @Column(length = 30, nullable = false)
    private String allocationNo;

    @NotNull
    @Column(nullable = false)
    private Integer itemNo;

    @NotNull
    @Size(max = 20)
    @Column(length = 20, nullable = false)
    private String locationCode;

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
    private Integer requestedQty;

    @Column
    private Integer allocatedQty;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private AllocationStatus status;
}
