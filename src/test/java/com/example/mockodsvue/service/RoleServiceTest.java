package com.example.mockodsvue.service;

import com.example.mockodsvue.model.dto.auth.AssignRoleRequest;
import com.example.mockodsvue.model.dto.auth.CreateRoleRequest;
import com.example.mockodsvue.model.entity.auth.AuthRole;
import com.example.mockodsvue.model.entity.auth.AuthUser;
import com.example.mockodsvue.model.entity.auth.AuthUserRole;
import com.example.mockodsvue.repository.AuthRoleRepository;
import com.example.mockodsvue.repository.AuthUserRepository;
import com.example.mockodsvue.repository.AuthUserRoleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("RoleService 測試")
class RoleServiceTest {

    @Mock
    private AuthRoleRepository authRoleRepository;

    @Mock
    private AuthUserRepository authUserRepository;

    @Mock
    private AuthUserRoleRepository authUserRoleRepository;

    @InjectMocks
    private RoleService roleService;

    private AuthRole testRole;
    private AuthUser testUser;

    @BeforeEach
    void setUp() {
        testRole = new AuthRole();
        testRole.setId(1);
        testRole.setRoleCode("ADMIN");
        testRole.setRoleName("管理員");

        testUser = new AuthUser();
        testUser.setId(1);
        testUser.setEmpNo("E001");
        testUser.setEmpName("測試使用者");
    }

    @Test
    @DisplayName("新增角色成功")
    void createRole_Success() {
        // given
        CreateRoleRequest request = new CreateRoleRequest();
        request.setRoleCode("ADMIN");
        request.setRoleName("管理員");

        when(authRoleRepository.findByRoleCode("ADMIN")).thenReturn(Optional.empty());
        when(authRoleRepository.save(any(AuthRole.class))).thenReturn(testRole);

        // when
        AuthRole result = roleService.createRole(request);

        // then
        assertNotNull(result);
        assertEquals("ADMIN", result.getRoleCode());
        assertEquals("管理員", result.getRoleName());
    }

    @Test
    @DisplayName("新增角色失敗 - 角色代碼已存在")
    void createRole_DuplicateRoleCode_ThrowsException() {
        // given
        CreateRoleRequest request = new CreateRoleRequest();
        request.setRoleCode("ADMIN");

        when(authRoleRepository.findByRoleCode("ADMIN")).thenReturn(Optional.of(testRole));

        // when & then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> roleService.createRole(request)
        );

        assertEquals("角色代碼已存在: ADMIN", exception.getMessage());
        verify(authRoleRepository, never()).save(any());
    }

    @Test
    @DisplayName("查詢所有角色")
    void getAllRoles_Success() {
        // given
        AuthRole role2 = new AuthRole();
        role2.setId(2);
        role2.setRoleCode("SALES");
        role2.setRoleName("業務員");

        when(authRoleRepository.findAll()).thenReturn(List.of(testRole, role2));

        // when
        List<AuthRole> roles = roleService.getAllRoles();

        // then
        assertEquals(2, roles.size());
    }

    @Test
    @DisplayName("刪除角色成功")
    void deleteRole_Success() {
        // given
        when(authRoleRepository.findByRoleCode("ADMIN")).thenReturn(Optional.of(testRole));

        // when
        roleService.deleteRole("ADMIN");

        // then
        verify(authUserRoleRepository).deleteByRoleCode("ADMIN");
        verify(authRoleRepository).delete(testRole);
    }

    @Test
    @DisplayName("刪除角色失敗 - 角色不存在")
    void deleteRole_NotFound_ThrowsException() {
        // given
        when(authRoleRepository.findByRoleCode("UNKNOWN")).thenReturn(Optional.empty());

        // when & then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> roleService.deleteRole("UNKNOWN")
        );

        assertEquals("找不到角色: UNKNOWN", exception.getMessage());
    }

    @Test
    @DisplayName("指派角色給使用者成功")
    void assignRoleToUser_Success() {
        // given
        AssignRoleRequest request = new AssignRoleRequest();
        request.setEmpNo("E001");
        request.setRoleCode("ADMIN");

        when(authUserRepository.findByEmpNo("E001")).thenReturn(Optional.of(testUser));
        when(authRoleRepository.findByRoleCode("ADMIN")).thenReturn(Optional.of(testRole));
        when(authUserRoleRepository.findByEmpNo("E001")).thenReturn(List.of());

        // when
        roleService.assignRoleToUser(request);

        // then
        ArgumentCaptor<AuthUserRole> captor = ArgumentCaptor.forClass(AuthUserRole.class);
        verify(authUserRoleRepository).save(captor.capture());
        assertEquals("E001", captor.getValue().getEmpNo());
        assertEquals("ADMIN", captor.getValue().getRoleCode());
    }

    @Test
    @DisplayName("指派角色失敗 - 使用者不存在")
    void assignRoleToUser_UserNotFound_ThrowsException() {
        // given
        AssignRoleRequest request = new AssignRoleRequest();
        request.setEmpNo("E999");
        request.setRoleCode("ADMIN");

        when(authUserRepository.findByEmpNo("E999")).thenReturn(Optional.empty());

        // when & then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> roleService.assignRoleToUser(request)
        );

        assertEquals("找不到使用者: E999", exception.getMessage());
    }

    @Test
    @DisplayName("指派角色失敗 - 角色不存在")
    void assignRoleToUser_RoleNotFound_ThrowsException() {
        // given
        AssignRoleRequest request = new AssignRoleRequest();
        request.setEmpNo("E001");
        request.setRoleCode("UNKNOWN");

        when(authUserRepository.findByEmpNo("E001")).thenReturn(Optional.of(testUser));
        when(authRoleRepository.findByRoleCode("UNKNOWN")).thenReturn(Optional.empty());

        // when & then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> roleService.assignRoleToUser(request)
        );

        assertEquals("找不到角色: UNKNOWN", exception.getMessage());
    }

    @Test
    @DisplayName("指派角色失敗 - 使用者已有此角色")
    void assignRoleToUser_AlreadyHasRole_ThrowsException() {
        // given
        AssignRoleRequest request = new AssignRoleRequest();
        request.setEmpNo("E001");
        request.setRoleCode("ADMIN");

        AuthUserRole existingRole = new AuthUserRole();
        existingRole.setEmpNo("E001");
        existingRole.setRoleCode("ADMIN");

        when(authUserRepository.findByEmpNo("E001")).thenReturn(Optional.of(testUser));
        when(authRoleRepository.findByRoleCode("ADMIN")).thenReturn(Optional.of(testRole));
        when(authUserRoleRepository.findByEmpNo("E001")).thenReturn(List.of(existingRole));

        // when & then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> roleService.assignRoleToUser(request)
        );

        assertEquals("使用者已擁有此角色", exception.getMessage());
    }

    @Test
    @DisplayName("移除使用者角色成功")
    void removeRoleFromUser_Success() {
        // given
        AuthUserRole userRole = new AuthUserRole();
        userRole.setId(1);
        userRole.setEmpNo("E001");
        userRole.setRoleCode("ADMIN");

        when(authUserRoleRepository.findByEmpNoAndRoleCode("E001", "ADMIN"))
                .thenReturn(Optional.of(userRole));

        // when
        roleService.removeRoleFromUser("E001", "ADMIN");

        // then
        verify(authUserRoleRepository).delete(userRole);
    }

    @Test
    @DisplayName("移除使用者角色失敗 - 使用者沒有此角色")
    void removeRoleFromUser_NotFound_ThrowsException() {
        // given
        when(authUserRoleRepository.findByEmpNoAndRoleCode("E001", "ADMIN"))
                .thenReturn(Optional.empty());

        // when & then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> roleService.removeRoleFromUser("E001", "ADMIN")
        );

        assertEquals("使用者沒有此角色", exception.getMessage());
    }
}
