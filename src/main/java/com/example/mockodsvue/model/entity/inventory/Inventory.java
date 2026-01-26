package com.example.mockodsvue.model.entity.inventory;

import com.example.mockodsvue.model.enums.LocationType;
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
@Table(name = "inventory", uniqueConstraints = @UniqueConstraint(columnNames = {"branch_code", "location_code", "product_code", "batch_no"}))
public class Inventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull
    @Size(max = 20)
    @Column(length = 20, nullable = false)
    private String branchCode;

    @NotNull
    @Size(max = 20)
    @Column(length = 20, nullable = false)
    private String locationCode;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private LocationType locationType;

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
    @Column(nullable = false, columnDefinition = "integer default 0")
    private Integer keepQty = 0;

    @NotNull
    @Column(nullable = false, columnDefinition = "integer default 0")
    private Integer returnQty = 0;
}
