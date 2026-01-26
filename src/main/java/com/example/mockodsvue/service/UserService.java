package com.example.mockodsvue.service;

import com.example.mockodsvue.model.dto.auth.CreateUserRequest;
import com.example.mockodsvue.model.dto.auth.UserResponse;
import com.example.mockodsvue.model.entity.auth.AuthUser;
import com.example.mockodsvue.model.entity.auth.AuthUserBranchRole;
import com.example.mockodsvue.repository.AuthUserRepository;
import com.example.mockodsvue.repository.AuthUserBranchRoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final AuthUserRepository authUserRepository;
    private final AuthUserBranchRoleRepository authUserBranchRoleRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * 新增使用者
     */
    @Transactional
    public UserResponse createUser(CreateUserRequest request) {
        // 檢查使用者編號是否已存在
        if (authUserRepository.findByUserId(request.getUserId()).isPresent()) {
            throw new IllegalArgumentException("使用者編號已存在: " + request.getUserId());
        }

        // 建立使用者
        AuthUser user = new AuthUser();
        user.setUserId(request.getUserId());
        user.setEmail(request.getEmail());
        user.setUserName(request.getUserName());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setBranchCode(request.getBranchCode());
        user.setStatus("ACTIVE");

        AuthUser savedUser = authUserRepository.save(user);

        return toUserResponse(savedUser, List.of());
    }

    /**
     * 查詢所有使用者
     */
    public List<UserResponse> getAllUsers() {
        return authUserRepository.findAll().stream()
                .map(user -> {
                    List<String> roles = getRolesByUserId(user.getUserId());
                    return toUserResponse(user, roles);
                })
                .toList();
    }

    /**
     * 根據使用者編號查詢使用者
     */
    public UserResponse getUserByUserId(String userId) {
        AuthUser user = authUserRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("找不到使用者: " + userId));

        List<String> roles = getRolesByUserId(userId);
        return toUserResponse(user, roles);
    }

    /**
     * 刪除使用者
     */
    @Transactional
    public void deleteUser(String userId) {
        AuthUser user = authUserRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("找不到使用者: " + userId));

        // 先刪除使用者的角色關聯
        authUserBranchRoleRepository.deleteByUserId(userId);
        // 再刪除使用者
        authUserRepository.delete(user);
    }

    /**
     * 取得使用者的角色清單
     */
    private List<String> getRolesByUserId(String userId) {
        return authUserBranchRoleRepository.findByUserId(userId).stream()
                .map(AuthUserBranchRole::getRoleCode)
                .toList();
    }

    /**
     * 轉換為 Response DTO
     */
    private UserResponse toUserResponse(AuthUser user, List<String> roles) {
        return UserResponse.builder()
                .id(user.getId())
                .userId(user.getUserId())
                .email(user.getEmail())
                .userName(user.getUserName())
                .branchCode(user.getBranchCode())
                .roles(roles)
                .build();
    }
}
