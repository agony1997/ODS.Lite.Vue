package com.example.mockodsvue.auth.model.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateUserRequest {

    @NotBlank(message = "使用者編號不可為空")
    @Size(max = 20, message = "使用者編號最多 20 字")
    private String userCode;

    @NotBlank(message = "Email 不可為空")
    @Email(message = "Email 格式不正確")
    @Size(max = 50, message = "Email 最多 50 字")
    private String email;

    @NotBlank(message = "姓名不可為空")
    @Size(max = 15, message = "姓名最多 15 字")
    private String userName;

    @NotBlank(message = "密碼不可為空")
    @Size(min = 6, max = 50, message = "密碼長度 6-50 字")
    private String password;

    @Size(max = 20, message = "營業所代碼最多 20 字")
    private String branchCode;
}
