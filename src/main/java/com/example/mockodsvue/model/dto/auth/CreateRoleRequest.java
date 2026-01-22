package com.example.mockodsvue.model.dto.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateRoleRequest {

    @NotBlank(message = "角色代碼不可為空")
    @Size(max = 20, message = "角色代碼最多 20 字")
    private String roleCode;

    @Size(max = 20, message = "角色名稱最多 20 字")
    private String roleName;
}
