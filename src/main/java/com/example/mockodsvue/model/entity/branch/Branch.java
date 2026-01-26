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
@Table(name = "branch")
public class Branch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Integer id;

    @NotNull
    @Size(max = 20)
    @Column(unique = true, length = 20, nullable = false)
    private String branchCode;

    @NotNull
    @Size(max = 40)
    @Column(length = 40, nullable = false)
    private String branchName;

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
