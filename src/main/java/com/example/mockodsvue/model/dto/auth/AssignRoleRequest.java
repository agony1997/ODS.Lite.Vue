package com.example.mockodsvue.model.dto.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AssignRoleRequest {

    @NotBlank(message = "使用者編號不可為空")
    private String userId;

    @NotBlank(message = "營業所代碼不可為空")
    private String branchCode;

    @NotBlank(message = "角色代碼不可為空")
    private String roleCode;
}
