package com.example.mockodsvue.service;

import com.example.mockodsvue.model.dto.auth.AssignRoleRequest;
import com.example.mockodsvue.model.dto.auth.CreateRoleRequest;
import com.example.mockodsvue.model.entity.auth.AuthRole;
import com.example.mockodsvue.model.entity.auth.AuthUserRole;
import com.example.mockodsvue.repository.AuthRoleRepository;
import com.example.mockodsvue.repository.AuthUserRepository;
import com.example.mockodsvue.repository.AuthUserRoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoleService {

    private final AuthRoleRepository authRoleRepository;
    private final AuthUserRepository authUserRepository;
    private final AuthUserRoleRepository authUserRoleRepository;

    /**
     * 新增角色
     */
    @Transactional
    public AuthRole createRole(CreateRoleRequest request) {
        // 檢查角色代碼是否已存在
        if (authRoleRepository.findByRoleCode(request.getRoleCode()).isPresent()) {
            throw new IllegalArgumentException("角色代碼已存在: " + request.getRoleCode());
        }

        AuthRole role = new AuthRole();
        role.setRoleCode(request.getRoleCode());
        role.setRoleName(request.getRoleName());

        return authRoleRepository.save(role);
    }

    /**
     * 查詢所有角色
     */
    public List<AuthRole> getAllRoles() {
        return authRoleRepository.findAll();
    }

    /**
     * 刪除角色
     */
    @Transactional
    public void deleteRole(String roleCode) {
        AuthRole role = authRoleRepository.findByRoleCode(roleCode)
                .orElseThrow(() -> new IllegalArgumentException("找不到角色: " + roleCode));

        // 先刪除角色關聯
        authUserRoleRepository.deleteByRoleCode(roleCode);
        // 再刪除角色
        authRoleRepository.delete(role);
    }

    /**
     * 指派角色給使用者
     */
    @Transactional
    public void assignRoleToUser(AssignRoleRequest request) {
        // 檢查使用者是否存在
        authUserRepository.findByEmpNo(request.getEmpNo())
                .orElseThrow(() -> new IllegalArgumentException("找不到使用者: " + request.getEmpNo()));

        // 檢查角色是否存在
        authRoleRepository.findByRoleCode(request.getRoleCode())
                .orElseThrow(() -> new IllegalArgumentException("找不到角色: " + request.getRoleCode()));

        // 檢查是否已經有這個角色
        boolean exists = authUserRoleRepository.findByEmpNo(request.getEmpNo()).stream()
                .anyMatch(ur -> ur.getRoleCode().equals(request.getRoleCode()));

        if (exists) {
            throw new IllegalArgumentException("使用者已擁有此角色");
        }

        // 新增角色關聯
        AuthUserRole userRole = new AuthUserRole();
        userRole.setEmpNo(request.getEmpNo());
        userRole.setRoleCode(request.getRoleCode());
        authUserRoleRepository.save(userRole);
    }

    /**
     * 移除使用者的角色
     */
    @Transactional
    public void removeRoleFromUser(String empNo, String roleCode) {
        AuthUserRole userRole = authUserRoleRepository.findByEmpNoAndRoleCode(empNo, roleCode)
                .orElseThrow(() -> new IllegalArgumentException("使用者沒有此角色"));

        authUserRoleRepository.delete(userRole);
    }
}
