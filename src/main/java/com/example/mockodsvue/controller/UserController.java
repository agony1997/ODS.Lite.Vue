package com.example.mockodsvue.controller;

import com.example.mockodsvue.model.dto.auth.CreateUserRequest;
import com.example.mockodsvue.model.dto.auth.UserResponse;
import com.example.mockodsvue.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * 新增使用者
     * POST /api/users
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody CreateUserRequest request) {
        UserResponse response = userService.createUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * 查詢所有使用者
     * GET /api/users
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        List<UserResponse> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    /**
     * 根據員工編號查詢使用者
     * GET /api/users/{empNo}
     */
    @GetMapping("/{empNo}")
    @PreAuthorize("hasRole('ADMIN') or #empNo == authentication.name")
    public ResponseEntity<UserResponse> getUser(@PathVariable String empNo) {
        UserResponse user = userService.getUserByEmpNo(empNo);
        return ResponseEntity.ok(user);
    }

    /**
     * 刪除使用者
     * DELETE /api/users/{empNo}
     */
    @DeleteMapping("/{empNo}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteUser(@PathVariable String empNo) {
        userService.deleteUser(empNo);
        return ResponseEntity.noContent().build();
    }
}
