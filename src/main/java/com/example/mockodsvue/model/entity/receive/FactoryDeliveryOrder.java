package com.example.mockodsvue.model.entity.receive;

import com.example.mockodsvue.model.entity.BaseEntity;
import com.example.mockodsvue.model.enums.DeliveryStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "factory_delivery_order")
public class FactoryDeliveryOrder extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull
    @Size(max = 30)
    @Column(unique = true, length = 30, nullable = false)
    private String fdoNo;

    @NotNull
    @Size(max = 30)
    @Column(length = 30, nullable = false)
    private String bpoNo;

    @NotNull
    @Size(max = 20)
    @Column(length = 20, nullable = false)
    private String branchCode;

    @NotNull
    @Size(max = 20)
    @Column(length = 20, nullable = false)
    private String factoryCode;

    @NotNull
    @Column(nullable = false)
    private LocalDate deliveryDate;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private DeliveryStatus status;

    private LocalDateTime receivedAt;

    @Size(max = 20)
    @Column(length = 20)
    private String receivedBy;
}
