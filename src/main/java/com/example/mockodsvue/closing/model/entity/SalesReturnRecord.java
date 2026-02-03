package com.example.mockodsvue.closing.model.entity;

import com.example.mockodsvue.shared.model.AuditEntity;
import com.example.mockodsvue.closing.model.enums.ReturnProcessStatus;
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
@Table(name = "sales_return_record")
public class SalesReturnRecord extends AuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull
    @Size(max = 30)
    @Column(unique = true, length = 30, nullable = false)
    private String returnNo;

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
    private LocalDate returnDate;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private ReturnProcessStatus status;
}
