package com.example.mockodsvue.auth.service;

import com.example.mockodsvue.auth.model.dto.LoginRequest;
import com.example.mockodsvue.auth.model.dto.LoginResponse;
import com.example.mockodsvue.auth.model.entity.AuthUser;
import com.example.mockodsvue.auth.model.entity.AuthUserBranchRole;
import com.example.mockodsvue.auth.repository.AuthUserRepository;
import com.example.mockodsvue.auth.repository.AuthUserBranchRoleRepository;
import com.example.mockodsvue.shared.security.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthService 測試")
class AuthServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private AuthUserRepository authUserRepository;

    @Mock
    private AuthUserBranchRoleRepository authUserBranchRoleRepository;

    @InjectMocks
    private AuthService authService;

    private AuthUser testUser;
    private LoginRequest loginRequest;

    @BeforeEach
    void setUp() {
        testUser = new AuthUser();
        testUser.setId(1);
        testUser.setUserCode("E001");
        testUser.setUserName("測試使用者");
        testUser.setEmail("test@example.com");
        testUser.setPassword("encodedPassword");
        testUser.setStatus("ACTIVE");

        loginRequest = new LoginRequest();
        loginRequest.setUserCode("E001");
        loginRequest.setPassword("password123");
    }

    @Test
    @DisplayName("登入成功")
    void login_Success() {
        // given
        Authentication authentication = mock(Authentication.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(authUserRepository.findByUserCode("E001")).thenReturn(Optional.of(testUser));

        AuthUserBranchRole role = new AuthUserBranchRole();
        role.setUserCode("E001");
        role.setBranchCode("B001");
        role.setRoleCode("ADMIN");
        when(authUserBranchRoleRepository.findByUserCode("E001")).thenReturn(List.of(role));
        when(jwtTokenProvider.generateToken(eq("E001"), anyList())).thenReturn("test-jwt-token");

        // when
        LoginResponse response = authService.login(loginRequest);

        // then
        assertNotNull(response);
        assertEquals("test-jwt-token", response.getToken());
        assertEquals("E001", response.getUserCode());
        assertEquals("測試使用者", response.getUserName());
        assertEquals(1, response.getRoles().size());
        assertTrue(response.getRoles().contains("ADMIN"));

        verify(authenticationManager).authenticate(any());
        verify(jwtTokenProvider).generateToken(eq("E001"), anyList());
    }

    @Test
    @DisplayName("登入失敗 - 帳號或密碼錯誤")
    void login_InvalidCredentials_ThrowsException() {
        // given
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        // when & then
        BadCredentialsException exception = assertThrows(
                BadCredentialsException.class,
                () -> authService.login(loginRequest)
        );

        assertEquals("帳號或密碼錯誤", exception.getMessage());
        verify(jwtTokenProvider, never()).generateToken(any(), any());
    }

    @Test
    @DisplayName("登入成功 - 使用者無角色")
    void login_UserWithNoRoles_Success() {
        // given
        Authentication authentication = mock(Authentication.class);
        when(authenticationManager.authenticate(any())).thenReturn(authentication);
        when(authUserRepository.findByUserCode("E001")).thenReturn(Optional.of(testUser));
        when(authUserBranchRoleRepository.findByUserCode("E001")).thenReturn(List.of());
        when(jwtTokenProvider.generateToken(eq("E001"), anyList())).thenReturn("test-jwt-token");

        // when
        LoginResponse response = authService.login(loginRequest);

        // then
        assertNotNull(response);
        assertTrue(response.getRoles().isEmpty());
    }
}
