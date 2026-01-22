package com.example.mockodsvue.model.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateUserRequest {

    @NotBlank(message = "員工編號不可為空")
    @Size(max = 20, message = "員工編號最多 20 字")
    private String empNo;

    @NotBlank(message = "Email 不可為空")
    @Email(message = "Email 格式不正確")
    @Size(max = 50, message = "Email 最多 50 字")
    private String email;

    @NotBlank(message = "姓名不可為空")
    @Size(max = 15, message = "姓名最多 15 字")
    private String empName;

    @NotBlank(message = "密碼不可為空")
    @Size(min = 6, max = 50, message = "密碼長度 6-50 字")
    private String password;
}
