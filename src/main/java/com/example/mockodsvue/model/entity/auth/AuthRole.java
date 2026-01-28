package com.example.mockodsvue.model.entity.auth;

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
@Table(name = "auth_role")
public class AuthRole extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull
    @Size(max = 20)
    @Column(unique = true, length = 20, nullable = false)
    private String roleCode;

    @Size(max = 20)
    @Column(nullable = false, length = 20)
    private String roleName;
}
