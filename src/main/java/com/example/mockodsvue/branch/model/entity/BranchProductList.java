package com.example.mockodsvue.branch.model.entity;

import com.example.mockodsvue.shared.model.AuditEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "branch_product_list", uniqueConstraints = @UniqueConstraint(columnNames = {"branch_code", "product_code", "unit"}))
public class BranchProductList extends AuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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
    @Column(length = 40, nullable = false)
    private String productName;

    @NotNull
    @Size(max = 5)
    @Column(length = 5, nullable = false)
    private String unit;

    @NotNull
    @Column(nullable = false, columnDefinition = "integer default 0")
    private int sortOrder;
}
