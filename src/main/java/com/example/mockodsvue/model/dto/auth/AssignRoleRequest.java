package com.example.mockodsvue.model.dto.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AssignRoleRequest {

    @NotBlank(message = "員工編號不可為空")
    private String empNo;

    @NotBlank(message = "角色代碼不可為空")
    private String roleCode;
}
