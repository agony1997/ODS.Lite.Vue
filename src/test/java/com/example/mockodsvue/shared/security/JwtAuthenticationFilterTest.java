package com.example.mockodsvue.shared.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.security.core.userdetails.UserDetails;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("JwtAuthenticationFilter 測試")
class JwtAuthenticationFilterTest {

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    private MockHttpServletRequest request;
    private MockHttpServletResponse response;

    @BeforeEach
    void setUp() {
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("合法 Token 設定 SecurityContext")
    void doFilter_ValidToken_SetsAuthentication() throws ServletException, IOException {
        // given
        String token = "valid.jwt.token";
        request.addHeader("Authorization", "Bearer " + token);

        when(jwtTokenProvider.validateToken(token)).thenReturn(true);
        when(jwtTokenProvider.getEmpNoFromToken(token)).thenReturn("E001");
        when(jwtTokenProvider.getRolesFromToken(token)).thenReturn(List.of("ADMIN", "SALES"));

        // when
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // then
        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        assertInstanceOf(UserDetails.class, principal);
        assertEquals("E001", ((UserDetails) principal).getUsername());
        assertEquals(2, SecurityContextHolder.getContext().getAuthentication().getAuthorities().size());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    @DisplayName("無 Token 繼續執行但不設定認證")
    void doFilter_NoToken_ContinuesWithoutAuthentication() throws ServletException, IOException {
        // given — 不設定 Authorization header

        // when
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // then
        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    @DisplayName("無效 Token 繼續執行但不設定認證")
    void doFilter_InvalidToken_ContinuesWithoutAuthentication() throws ServletException, IOException {
        // given
        String token = "invalid.jwt.token";
        request.addHeader("Authorization", "Bearer " + token);

        when(jwtTokenProvider.validateToken(token)).thenReturn(false);

        // when
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // then
        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    @DisplayName("格式錯誤的 Authorization Header 繼續執行但不設定認證")
    void doFilter_MalformedAuthorizationHeader_ContinuesWithoutAuthentication() throws ServletException, IOException {
        // given — 不含 "Bearer " 前綴
        request.addHeader("Authorization", "Basic some-credentials");

        // when
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // then
        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain).doFilter(request, response);
    }
}
