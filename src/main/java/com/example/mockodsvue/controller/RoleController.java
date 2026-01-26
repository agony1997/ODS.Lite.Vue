package com.example.mockodsvue.controller;

import com.example.mockodsvue.model.dto.auth.AssignRoleRequest;
import com.example.mockodsvue.model.dto.auth.CreateRoleRequest;
import com.example.mockodsvue.model.entity.auth.AuthRole;
import com.example.mockodsvue.service.RoleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")  // 整個 Controller 都需要 ADMIN 權限
public class RoleController {

    private final RoleService roleService;

    /**
     * 新增角色
     * POST /api/roles
     */
    @PostMapping
    public ResponseEntity<AuthRole> createRole(@Valid @RequestBody CreateRoleRequest request) {
        AuthRole role = roleService.createRole(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(role);
    }

    /**
     * 查詢所有角色
     * GET /api/roles
     */
    @GetMapping
    public ResponseEntity<List<AuthRole>> getAllRoles() {
        List<AuthRole> roles = roleService.getAllRoles();
        return ResponseEntity.ok(roles);
    }

    /**
     * 刪除角色
     * DELETE /api/roles/{roleCode}
     */
    @DeleteMapping("/{roleCode}")
    public ResponseEntity<Void> deleteRole(@PathVariable String roleCode) {
        roleService.deleteRole(roleCode);
        return ResponseEntity.noContent().build();
    }

    /**
     * 指派角色給使用者
     * POST /api/roles/assign
     */
    @PostMapping("/assign")
    public ResponseEntity<Void> assignRole(@Valid @RequestBody AssignRoleRequest request) {
        roleService.assignRoleToUser(request);
        return ResponseEntity.ok().build();
    }

    /**
     * 移除使用者的角色
     * DELETE /api/roles/assign/{userId}/{branchCode}/{roleCode}
     */
    @DeleteMapping("/assign/{userId}/{branchCode}/{roleCode}")
    public ResponseEntity<Void> removeRole(
            @PathVariable String userId,
            @PathVariable String branchCode,
            @PathVariable String roleCode) {
        roleService.removeRoleFromUser(userId, branchCode, roleCode);
        return ResponseEntity.noContent().build();
    }
}
