package com.example.mockodsvue.master.model.entity;

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
@Table(name = "customer")
public class Customer extends AuditEntity {

    @Id
    @Size(max = 20)
    @Column(length = 20)
    private String customerCode;

    @NotNull
    @Size(max = 100)
    @Column(length = 100, nullable = false)
    private String customerName;

    @NotNull
    @Size(max = 20)
    @Column(length = 20, nullable = false)
    private String branchCode;

    @Size(max = 200)
    @Column(length = 200)
    private String address;

    @Size(max = 20)
    @Column(length = 20)
    private String phone;

    @NotNull
    @Size(max = 20)
    @Column(length = 20, nullable = false)
    private String status;
}
