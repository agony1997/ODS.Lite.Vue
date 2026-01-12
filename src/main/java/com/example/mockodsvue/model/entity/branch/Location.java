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
@Table(name = "location", uniqueConstraints = @UniqueConstraint(columnNames = {"branch_code", "location_code"}))
public class Location {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Integer id;

    @NotNull
    @Size(max = 20)
    @Column(length = 20, nullable = false)
    private String locationCode;

    @NotNull
    @Size(max = 40)
    @Column(length = 40, nullable = false)
    private String locationName;

    @NotNull
    @Size(max = 20)
    @Column(length = 20, nullable = false)
    private String branchCode;

    @NotNull
    @Size(max = 20)
    @Column(unique = true, length = 20, nullable = false)
    private String empNo;

    @NotNull
    @Size(max = 15)
    @Column(nullable = false, length = 15)
    private String empName;

    @NotNull
    @Column(nullable = false, columnDefinition = "boolean default false")
    private boolean isEnable;

}
