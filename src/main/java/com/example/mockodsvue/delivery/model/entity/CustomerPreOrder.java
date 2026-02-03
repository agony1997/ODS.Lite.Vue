package com.example.mockodsvue.delivery.model.entity;

import com.example.mockodsvue.shared.model.AuditEntity;
import com.example.mockodsvue.delivery.model.enums.PreOrderStatus;
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
@Table(name = "customer_pre_order")
public class CustomerPreOrder extends AuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull
    @Size(max = 30)
    @Column(unique = true, length = 30, nullable = false)
    private String preOrderNo;

    @NotNull
    @Size(max = 20)
    @Column(length = 20, nullable = false)
    private String branchCode;

    @NotNull
    @Size(max = 20)
    @Column(length = 20, nullable = false)
    private String locationCode;

    @NotNull
    @Size(max = 20)
    @Column(length = 20, nullable = false)
    private String customerCode;

    @NotNull
    @Column(nullable = false)
    private LocalDate orderDate;

    @NotNull
    @Column(nullable = false)
    private LocalDate deliveryDate;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private PreOrderStatus status;
}
