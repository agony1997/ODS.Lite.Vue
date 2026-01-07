package com.example.mockodsvue.model.entity.auth;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "auth_user_role", uniqueConstraints = @UniqueConstraint(columnNames = {"account_code", "role_code"}))
public class AuthUserRole {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull
    @Size(max = 20)
    @Column(unique = true, length = 20, nullable = false)
    private String accountCode;

    @NotNull
    @Size(max = 20)
    @Column(unique = true, length = 20, nullable = false)
    private String roleCode;
}
