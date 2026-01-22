package com.example.mockodsvue.service;

import com.example.mockodsvue.model.dto.auth.CreateUserRequest;
import com.example.mockodsvue.model.dto.auth.UserResponse;
import com.example.mockodsvue.model.entity.auth.AuthUser;
import com.example.mockodsvue.model.entity.auth.AuthUserRole;
import com.example.mockodsvue.repository.AuthUserRepository;
import com.example.mockodsvue.repository.AuthUserRoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final AuthUserRepository authUserRepository;
    private final AuthUserRoleRepository authUserRoleRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * 新增使用者
     */
    @Transactional
    public UserResponse createUser(CreateUserRequest request) {
        // 檢查員工編號是否已存在
        if (authUserRepository.findByEmpNo(request.getEmpNo()).isPresent()) {
            throw new IllegalArgumentException("員工編號已存在: " + request.getEmpNo());
        }

        // 建立使用者
        AuthUser user = new AuthUser();
        user.setEmpNo(request.getEmpNo());
        user.setEmail(request.getEmail());
        user.setEmpName(request.getEmpName());
        user.setPassword(passwordEncoder.encode(request.getPassword()));  // 密碼加密

        AuthUser savedUser = authUserRepository.save(user);

        return toUserResponse(savedUser, List.of());
    }

    /**
     * 查詢所有使用者
     */
    public List<UserResponse> getAllUsers() {
        return authUserRepository.findAll().stream()
                .map(user -> {
                    List<String> roles = getRolesByEmpNo(user.getEmpNo());
                    return toUserResponse(user, roles);
                })
                .toList();
    }

    /**
     * 根據員工編號查詢使用者
     */
    public UserResponse getUserByEmpNo(String empNo) {
        AuthUser user = authUserRepository.findByEmpNo(empNo)
                .orElseThrow(() -> new IllegalArgumentException("找不到使用者: " + empNo));

        List<String> roles = getRolesByEmpNo(empNo);
        return toUserResponse(user, roles);
    }

    /**
     * 刪除使用者
     */
    @Transactional
    public void deleteUser(String empNo) {
        AuthUser user = authUserRepository.findByEmpNo(empNo)
                .orElseThrow(() -> new IllegalArgumentException("找不到使用者: " + empNo));

        // 先刪除使用者的角色關聯
        authUserRoleRepository.deleteByEmpNo(empNo);
        // 再刪除使用者
        authUserRepository.delete(user);
    }

    /**
     * 取得使用者的角色清單
     */
    private List<String> getRolesByEmpNo(String empNo) {
        return authUserRoleRepository.findByEmpNo(empNo).stream()
                .map(AuthUserRole::getRoleCode)
                .toList();
    }

    /**
     * 轉換為 Response DTO
     */
    private UserResponse toUserResponse(AuthUser user, List<String> roles) {
        return UserResponse.builder()
                .id(user.getId())
                .empNo(user.getEmpNo())
                .email(user.getEmail())
                .empName(user.getEmpName())
                .roles(roles)
                .build();
    }
}
