package com.example.mockodsvue.model.entity.purchase;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

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

    @Column(nullable = false, columnDefinition = "boolean default false")
    private Boolean isFreeze = false;

}
