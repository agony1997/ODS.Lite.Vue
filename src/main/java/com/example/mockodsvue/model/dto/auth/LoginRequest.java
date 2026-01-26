package com.example.mockodsvue.model.dto.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {

    @NotBlank(message = "使用者編號不可為空")
    private String userId;

    @NotBlank(message = "密碼不可為空")
    private String password;
}
