package com.example.mockodsvue.model.entity.master;

import com.example.mockodsvue.model.entity.BaseEntity;
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
@Table(name = "factory")
public class Factory extends BaseEntity {

    @Id
    @Size(max = 20)
    @Column(length = 20)
    private String factoryCode;

    @NotNull
    @Size(max = 100)
    @Column(length = 100, nullable = false)
    private String factoryName;

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
