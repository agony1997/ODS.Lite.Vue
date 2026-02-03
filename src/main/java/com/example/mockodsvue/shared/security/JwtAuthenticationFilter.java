package com.example.mockodsvue.shared.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

/**
 * JWT 認證 Filter
 * 每個請求都會經過這個 Filter，驗證 Token 並設定使用者身份
 */
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        // 1. 從 Header 取得 Token
        String token = getTokenFromRequest(request);

        // 2. 驗證 Token 並設定身份
        if (StringUtils.hasText(token) && jwtTokenProvider.validateToken(token)) {

            // 3. 從 Token 取得使用者資訊
            String empNo = jwtTokenProvider.getEmpNoFromToken(token);
            List<String> roles = jwtTokenProvider.getRolesFromToken(token);

            // 4. 轉換角色為 Spring Security 格式
            List<SimpleGrantedAuthority> authorities = roles.stream()
                    .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                    .toList();

            // 5. 建立 UserDetails 作為 principal，讓 @AuthenticationPrincipal 能正確解析
            UserDetails userDetails = new User(empNo, "", authorities);

            // 6. 建立認證物件
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(userDetails, null, authorities);
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            // 7. 設定到 SecurityContext，讓 Spring Security 知道當前使用者
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        // 8. 繼續執行後續 Filter
        filterChain.doFilter(request, response);
    }

    /**
     * 從 Request Header 取得 Token
     * 格式: Authorization: Bearer <token>
     */
    private String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");

        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);  // 去掉 "Bearer " 前綴
        }

        return null;
    }
}
