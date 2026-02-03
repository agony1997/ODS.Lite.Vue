package com.example.mockodsvue.branch.model.entity;

import com.example.mockodsvue.shared.model.AuditEntity;
import com.example.mockodsvue.branch.model.enums.LocationType;
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
@Table(name = "location", uniqueConstraints = @UniqueConstraint(columnNames = {"branch_code", "location_code"}))
public class Location extends AuditEntity {

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

    @Size(max = 20)
    @Column(length = 20)
    private String userCode;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private LocationType locationType;

    @NotNull
    @Size(max = 20)
    @Column(length = 20, nullable = false)
    private String status;
}
