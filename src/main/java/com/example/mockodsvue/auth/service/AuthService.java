package com.example.mockodsvue.auth.service;

import com.example.mockodsvue.auth.model.dto.LoginRequest;
import com.example.mockodsvue.auth.model.dto.LoginResponse;
import com.example.mockodsvue.auth.model.entity.AuthUser;
import com.example.mockodsvue.auth.model.entity.AuthUserBranchRole;
import com.example.mockodsvue.auth.repository.AuthUserRepository;
import com.example.mockodsvue.auth.repository.AuthUserBranchRoleRepository;
import com.example.mockodsvue.shared.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthUserRepository authUserRepository;
    private final AuthUserBranchRoleRepository authUserBranchRoleRepository;

    /**
     * 登入
     */
    public LoginResponse login(LoginRequest request) {
        try {
            // 1. 使用 Spring Security 驗證帳密
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUserCode(),
                            request.getPassword()
                    )
            );

            // 2. 查詢使用者資料
            AuthUser user = authUserRepository.findByUserCode(request.getUserCode())
                    .orElseThrow(() -> new BadCredentialsException("使用者不存在"));

            // 3. 查詢角色
            List<String> roles = authUserBranchRoleRepository.findByUserCode(request.getUserCode())
                    .stream()
                    .map(AuthUserBranchRole::getRoleCode)
                    .toList();

            // 4. 產生 JWT Token
            String token = jwtTokenProvider.generateToken(user.getUserCode(), roles);

            // 5. 回傳登入結果
            return LoginResponse.builder()
                    .token(token)
                    .userCode(user.getUserCode())
                    .userName(user.getUserName())
                    .branchCode(user.getBranchCode())
                    .roles(roles)
                    .build();

        } catch (AuthenticationException e) {
            throw new BadCredentialsException("帳號或密碼錯誤");
        }
    }
}
