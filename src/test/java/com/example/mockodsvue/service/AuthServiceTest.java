package com.example.mockodsvue.service;

import com.example.mockodsvue.model.dto.auth.LoginRequest;
import com.example.mockodsvue.model.dto.auth.LoginResponse;
import com.example.mockodsvue.model.entity.auth.AuthUser;
import com.example.mockodsvue.model.entity.auth.AuthUserRole;
import com.example.mockodsvue.repository.AuthUserRepository;
import com.example.mockodsvue.repository.AuthUserRoleRepository;
import com.example.mockodsvue.security.JwtTokenProvider;
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
    private AuthUserRoleRepository authUserRoleRepository;

    @InjectMocks
    private AuthService authService;

    private AuthUser testUser;
    private LoginRequest loginRequest;

    @BeforeEach
    void setUp() {
        testUser = new AuthUser();
        testUser.setId(1);
        testUser.setEmpNo("E001");
        testUser.setEmpName("測試使用者");
        testUser.setEmail("test@example.com");
        testUser.setPassword("encodedPassword");

        loginRequest = new LoginRequest();
        loginRequest.setEmpNo("E001");
        loginRequest.setPassword("password123");
    }

    @Test
    @DisplayName("登入成功")
    void login_Success() {
        // given
        Authentication authentication = mock(Authentication.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(authUserRepository.findByEmpNo("E001")).thenReturn(Optional.of(testUser));

        AuthUserRole role = new AuthUserRole();
        role.setEmpNo("E001");
        role.setRoleCode("ADMIN");
        when(authUserRoleRepository.findByEmpNo("E001")).thenReturn(List.of(role));
        when(jwtTokenProvider.generateToken(eq("E001"), anyList())).thenReturn("test-jwt-token");

        // when
        LoginResponse response = authService.login(loginRequest);

        // then
        assertNotNull(response);
        assertEquals("test-jwt-token", response.getToken());
        assertEquals("E001", response.getEmpNo());
        assertEquals("測試使用者", response.getEmpName());
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
        when(authUserRepository.findByEmpNo("E001")).thenReturn(Optional.of(testUser));
        when(authUserRoleRepository.findByEmpNo("E001")).thenReturn(List.of());
        when(jwtTokenProvider.generateToken(eq("E001"), anyList())).thenReturn("test-jwt-token");

        // when
        LoginResponse response = authService.login(loginRequest);

        // then
        assertNotNull(response);
        assertTrue(response.getRoles().isEmpty());
    }
}
