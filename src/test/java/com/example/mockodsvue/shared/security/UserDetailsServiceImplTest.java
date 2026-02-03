package com.example.mockodsvue.shared.security;

import com.example.mockodsvue.auth.model.entity.AuthUser;
import com.example.mockodsvue.auth.model.entity.AuthUserBranchRole;
import com.example.mockodsvue.auth.repository.AuthUserBranchRoleRepository;
import com.example.mockodsvue.auth.repository.AuthUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserDetailsServiceImpl 測試")
class UserDetailsServiceImplTest {

    @Mock
    private AuthUserRepository authUserRepository;

    @Mock
    private AuthUserBranchRoleRepository authUserBranchRoleRepository;

    @InjectMocks
    private UserDetailsServiceImpl userDetailsService;

    private AuthUser testUser;

    @BeforeEach
    void setUp() {
        testUser = new AuthUser();
        testUser.setId(1);
        testUser.setUserCode("E001");
        testUser.setEmail("test@example.com");
        testUser.setUserName("測試使用者");
        testUser.setPassword("encodedPassword");
        testUser.setStatus("ACTIVE");
    }

    @Test
    @DisplayName("載入使用者成功 - 含角色")
    void loadUserByUsername_Success() {
        // given
        AuthUserBranchRole role1 = new AuthUserBranchRole();
        role1.setUserCode("E001");
        role1.setBranchCode("BR01");
        role1.setRoleCode("ADMIN");

        AuthUserBranchRole role2 = new AuthUserBranchRole();
        role2.setUserCode("E001");
        role2.setBranchCode("BR01");
        role2.setRoleCode("LEADER");

        when(authUserRepository.findByUserCode("E001")).thenReturn(Optional.of(testUser));
        when(authUserBranchRoleRepository.findByUserCode("E001")).thenReturn(List.of(role1, role2));

        // when
        UserDetails userDetails = userDetailsService.loadUserByUsername("E001");

        // then
        assertNotNull(userDetails);
        assertEquals("E001", userDetails.getUsername());
        assertEquals("encodedPassword", userDetails.getPassword());
        assertEquals(2, userDetails.getAuthorities().size());
        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")));
        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_LEADER")));
    }

    @Test
    @DisplayName("載入使用者失敗 - 使用者不存在")
    void loadUserByUsername_UserNotFound_ThrowsException() {
        // given
        when(authUserRepository.findByUserCode("E999")).thenReturn(Optional.empty());

        // when & then
        UsernameNotFoundException exception = assertThrows(
                UsernameNotFoundException.class,
                () -> userDetailsService.loadUserByUsername("E999")
        );
        assertEquals("找不到使用者: E999", exception.getMessage());
    }

    @Test
    @DisplayName("載入使用者成功 - 無角色")
    void loadUserByUsername_NoRoles_ReturnsEmptyAuthorities() {
        // given
        when(authUserRepository.findByUserCode("E001")).thenReturn(Optional.of(testUser));
        when(authUserBranchRoleRepository.findByUserCode("E001")).thenReturn(List.of());

        // when
        UserDetails userDetails = userDetailsService.loadUserByUsername("E001");

        // then
        assertNotNull(userDetails);
        assertEquals("E001", userDetails.getUsername());
        assertTrue(userDetails.getAuthorities().isEmpty());
    }
}
