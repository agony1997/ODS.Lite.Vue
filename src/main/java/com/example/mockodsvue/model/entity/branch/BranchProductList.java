package com.example.mockodsvue.model.entity.branch;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "branch_product_list", uniqueConstraints = @UniqueConstraint(columnNames = {"branch_code", "product_code", "unit"}))
public class BranchProductList {

    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    @Column(nullable = false)
    private Integer id;

    @NotNull
    @Size(max = 20)
    @Column(length = 20, nullable = false)
    private String branchCode;

    @NotNull
    @Size(max = 20)
    @Column(length = 20, nullable = false)
    private String productCode;

    @Size(max = 40)
    @Column(length = 20, nullable = false)
    private String productName;

    @NotNull
    @Size(max = 5)
    @Column(length = 5, nullable = false)
    private String unit;

    @NotNull
    @Column(nullable = false, columnDefinition = "integer default 0")
    private int sortOrder;

}
