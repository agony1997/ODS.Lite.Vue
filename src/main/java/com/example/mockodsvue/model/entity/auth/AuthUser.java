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
@Table(name = "auth_user")
public class AuthUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull
    @Size(max = 20)
    @Column(unique = true, length = 20, nullable = false)
    private String empNo;

    @NotNull
    @Size(max = 50)
    @Column(unique = true, length = 50, nullable = false)
    private String email;

    @NotNull
    @Size(max = 15)
    @Column(nullable = false, length = 15)
    private String empName;

    @NotNull
    @Size(max = 72)
    @Column(nullable = false, length = 72)
    private String password;

}
