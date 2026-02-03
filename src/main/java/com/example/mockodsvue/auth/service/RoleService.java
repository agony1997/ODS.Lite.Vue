package com.example.mockodsvue.auth.service;

import com.example.mockodsvue.auth.model.dto.AssignRoleRequest;
import com.example.mockodsvue.auth.model.dto.CreateRoleRequest;
import com.example.mockodsvue.auth.model.entity.AuthRole;
import com.example.mockodsvue.auth.model.entity.AuthUserBranchRole;
import com.example.mockodsvue.auth.repository.AuthRoleRepository;
import com.example.mockodsvue.auth.repository.AuthUserRepository;
import com.example.mockodsvue.auth.repository.AuthUserBranchRoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoleService {

    private final AuthRoleRepository authRoleRepository;
    private final AuthUserRepository authUserRepository;
    private final AuthUserBranchRoleRepository authUserBranchRoleRepository;

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
        authUserBranchRoleRepository.deleteByRoleCode(roleCode);
        // 再刪除角色
        authRoleRepository.delete(role);
    }

    /**
     * 指派角色給使用者
     */
    @Transactional
    public void assignRoleToUser(AssignRoleRequest request) {
        // 檢查使用者是否存在
        authUserRepository.findByUserCode(request.getUserCode())
                .orElseThrow(() -> new IllegalArgumentException("找不到使用者: " + request.getUserCode()));

        // 檢查角色是否存在
        authRoleRepository.findByRoleCode(request.getRoleCode())
                .orElseThrow(() -> new IllegalArgumentException("找不到角色: " + request.getRoleCode()));

        // 檢查是否已經有這個角色
        boolean exists = authUserBranchRoleRepository.findByUserCodeAndBranchCode(request.getUserCode(), request.getBranchCode()).stream()
                .anyMatch(ur -> ur.getRoleCode().equals(request.getRoleCode()));

        if (exists) {
            throw new IllegalArgumentException("使用者在此營業所已擁有此角色");
        }

        // 新增角色關聯
        AuthUserBranchRole userRole = new AuthUserBranchRole();
        userRole.setUserCode(request.getUserCode());
        userRole.setBranchCode(request.getBranchCode());
        userRole.setRoleCode(request.getRoleCode());
        authUserBranchRoleRepository.save(userRole);
    }

    /**
     * 移除使用者的角色
     */
    @Transactional
    public void removeRoleFromUser(String userCode, String branchCode, String roleCode) {
        AuthUserBranchRole userRole = authUserBranchRoleRepository.findByUserCodeAndBranchCodeAndRoleCode(userCode, branchCode, roleCode)
                .orElseThrow(() -> new IllegalArgumentException("使用者在此營業所沒有此角色"));

        authUserBranchRoleRepository.delete(userRole);
    }
}
