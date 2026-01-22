package com.example.mockodsvue.model.dto.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {

    @NotBlank(message = "員工編號不可為空")
    private String empNo;

    @NotBlank(message = "密碼不可為空")
    private String password;
}
