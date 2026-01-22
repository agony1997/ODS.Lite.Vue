package com.example.mockodsvue.security;

import com.example.mockodsvue.model.entity.auth.AuthUser;
import com.example.mockodsvue.model.entity.auth.AuthUserRole;
import com.example.mockodsvue.repository.AuthUserRepository;
import com.example.mockodsvue.repository.AuthUserRoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final AuthUserRepository authUserRepository;
    private final AuthUserRoleRepository authUserRoleRepository;

    /**
     * Spring Security 呼叫此方法載入使用者
     * @param empNo 員工編號（當作 username）
     */
    @Override
    public UserDetails loadUserByUsername(String empNo) throws UsernameNotFoundException {
        // 1. 從資料庫查詢使用者
        AuthUser authUser = authUserRepository.findByEmpNo(empNo)
                .orElseThrow(() -> new UsernameNotFoundException("找不到使用者: " + empNo));

        // 2. 查詢使用者的角色
        List<AuthUserRole> userRoles = authUserRoleRepository.findByEmpNo(empNo);

        // 3. 轉換角色為 Spring Security 的 GrantedAuthority
        List<SimpleGrantedAuthority> authorities = userRoles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getRoleCode()))
                .toList();

        // 4. 回傳 Spring Security 的 User 物件
        return new User(
                authUser.getEmpNo(),      // username
                authUser.getPassword(),   // password (已加密)
                authorities               // 權限清單
        );
    }
}
