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
     * 根據使用者編號查詢使用者
     * GET /api/users/{userId}
     */
    @GetMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN') or #userId == authentication.name")
    public ResponseEntity<UserResponse> getUser(@PathVariable String userId) {
        UserResponse user = userService.getUserByUserId(userId);
        return ResponseEntity.ok(user);
    }

    /**
     * 刪除使用者
     * DELETE /api/users/{userId}
     */
    @DeleteMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteUser(@PathVariable String userId) {
        userService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }
}
