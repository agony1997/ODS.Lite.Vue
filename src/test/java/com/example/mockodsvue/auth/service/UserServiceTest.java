package com.example.mockodsvue.auth.service;

import com.example.mockodsvue.auth.model.dto.CreateUserRequest;
import com.example.mockodsvue.auth.model.dto.UserResponse;
import com.example.mockodsvue.auth.model.entity.AuthUser;
import com.example.mockodsvue.auth.model.entity.AuthUserBranchRole;
import com.example.mockodsvue.auth.repository.AuthUserRepository;
import com.example.mockodsvue.auth.repository.AuthUserBranchRoleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserService 測試")
class UserServiceTest {

    @Mock
    private AuthUserRepository authUserRepository;

    @Mock
    private AuthUserBranchRoleRepository authUserBranchRoleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private CreateUserRequest createUserRequest;
    private AuthUser savedUser;

    @BeforeEach
    void setUp() {
        createUserRequest = new CreateUserRequest();
        createUserRequest.setUserCode("E001");
        createUserRequest.setEmail("test@example.com");
        createUserRequest.setUserName("測試使用者");
        createUserRequest.setPassword("password123");

        savedUser = new AuthUser();
        savedUser.setId(1);
        savedUser.setUserCode("E001");
        savedUser.setEmail("test@example.com");
        savedUser.setUserName("測試使用者");
        savedUser.setPassword("encodedPassword");
        savedUser.setStatus("ACTIVE");
    }

    @Test
    @DisplayName("新增使用者成功")
    void createUser_Success() {
        // given
        when(authUserRepository.findByUserCode("E001")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(authUserRepository.save(any(AuthUser.class))).thenReturn(savedUser);

        // when
        UserResponse response = userService.createUser(createUserRequest);

        // then
        assertNotNull(response);
        assertEquals("E001", response.getUserCode());
        assertEquals("測試使用者", response.getUserName());
        assertEquals("test@example.com", response.getEmail());

        ArgumentCaptor<AuthUser> captor = ArgumentCaptor.forClass(AuthUser.class);
        verify(authUserRepository).save(captor.capture());
        assertEquals("encodedPassword", captor.getValue().getPassword());
    }

    @Test
    @DisplayName("新增使用者失敗 - 使用者編號已存在")
    void createUser_DuplicateUserCode_ThrowsException() {
        // given
        when(authUserRepository.findByUserCode("E001")).thenReturn(Optional.of(savedUser));

        // when & then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> userService.createUser(createUserRequest)
        );

        assertEquals("使用者編號已存在: E001", exception.getMessage());
        verify(authUserRepository, never()).save(any());
    }

    @Test
    @DisplayName("查詢所有使用者")
    void getAllUsers_Success() {
        // given
        AuthUser user2 = new AuthUser();
        user2.setId(2);
        user2.setUserCode("E002");
        user2.setEmail("test2@example.com");
        user2.setUserName("測試使用者2");
        user2.setStatus("ACTIVE");

        when(authUserRepository.findAll()).thenReturn(List.of(savedUser, user2));
        when(authUserBranchRoleRepository.findByUserCode("E001")).thenReturn(List.of());
        when(authUserBranchRoleRepository.findByUserCode("E002")).thenReturn(List.of());

        // when
        List<UserResponse> users = userService.getAllUsers();

        // then
        assertEquals(2, users.size());
    }

    @Test
    @DisplayName("根據使用者編號查詢使用者")
    void getUserByUserCode_Success() {
        // given
        when(authUserRepository.findByUserCode("E001")).thenReturn(Optional.of(savedUser));

        AuthUserBranchRole role = new AuthUserBranchRole();
        role.setUserCode("E001");
        role.setBranchCode("B001");
        role.setRoleCode("ADMIN");
        when(authUserBranchRoleRepository.findByUserCode("E001")).thenReturn(List.of(role));

        // when
        UserResponse response = userService.getUserByUserCode("E001");

        // then
        assertNotNull(response);
        assertEquals("E001", response.getUserCode());
        assertEquals(1, response.getRoles().size());
        assertTrue(response.getRoles().contains("ADMIN"));
    }

    @Test
    @DisplayName("查詢使用者失敗 - 使用者不存在")
    void getUserByUserCode_NotFound_ThrowsException() {
        // given
        when(authUserRepository.findByUserCode("E999")).thenReturn(Optional.empty());

        // when & then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> userService.getUserByUserCode("E999")
        );

        assertEquals("找不到使用者: E999", exception.getMessage());
    }

    @Test
    @DisplayName("刪除使用者成功")
    void deleteUser_Success() {
        // given
        when(authUserRepository.findByUserCode("E001")).thenReturn(Optional.of(savedUser));

        // when
        userService.deleteUser("E001");

        // then
        verify(authUserBranchRoleRepository).deleteByUserCode("E001");
        verify(authUserRepository).delete(savedUser);
    }

    @Test
    @DisplayName("刪除使用者失敗 - 使用者不存在")
    void deleteUser_NotFound_ThrowsException() {
        // given
        when(authUserRepository.findByUserCode("E999")).thenReturn(Optional.empty());

        // when & then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> userService.deleteUser("E999")
        );

        assertEquals("找不到使用者: E999", exception.getMessage());
        verify(authUserRepository, never()).delete(any());
    }
}
