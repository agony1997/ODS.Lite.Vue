package com.example.mockodsvue.auth.model.entity;

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
@Table(name = "auth_user")
public class AuthUser extends AuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull
    @Size(max = 20)
    @Column(name = "user_code", unique = true, length = 20, nullable = false)
    private String userCode;

    @NotNull
    @Size(max = 50)
    @Column(unique = true, length = 50, nullable = false)
    private String email;

    @NotNull
    @Size(max = 15)
    @Column(name = "user_name", nullable = false, length = 15)
    private String userName;

    @NotNull
    @Size(max = 72)
    @Column(nullable = false, length = 72)
    private String password;

    @Size(max = 20)
    @Column(length = 20)
    private String branchCode;

    @Size(max = 20)
    @Column(length = 20)
    private String phone;

    @NotNull
    @Size(max = 20)
    @Column(length = 20, nullable = false)
    private String status;
}
