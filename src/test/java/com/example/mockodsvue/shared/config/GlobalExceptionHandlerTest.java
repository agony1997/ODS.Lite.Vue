package com.example.mockodsvue.shared.config;

import com.example.mockodsvue.shared.exception.BusinessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@DisplayName("GlobalExceptionHandler 測試")
class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
    }

    @Test
    @DisplayName("BusinessException 回傳正確狀態碼與訊息")
    void handleBusinessException_ReturnsCorrectStatusAndBody() {
        // given
        BusinessException ex = new BusinessException("營業所不存在");

        // when
        ResponseEntity<Map<String, Object>> response = handler.handleBusinessException(ex);

        // then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("營業所不存在", response.getBody().get("message"));
        assertEquals(400, response.getBody().get("status"));
    }

    @Test
    @DisplayName("BadCredentialsException 回傳 401")
    void handleBadCredentials_Returns401() {
        // given
        BadCredentialsException ex = new BadCredentialsException("密碼錯誤");

        // when
        ResponseEntity<Map<String, Object>> response = handler.handleBadCredentials(ex);

        // then
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("密碼錯誤", response.getBody().get("message"));
        assertEquals(401, response.getBody().get("status"));
    }

    @Test
    @DisplayName("IllegalArgumentException 回傳 400")
    void handleIllegalArgument_Returns400() {
        // given
        IllegalArgumentException ex = new IllegalArgumentException("參數無效");

        // when
        ResponseEntity<Map<String, Object>> response = handler.handleIllegalArgument(ex);

        // then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("參數無效", response.getBody().get("message"));
    }

    @Test
    @DisplayName("MethodArgumentNotValidException 回傳 400 含欄位錯誤")
    void handleValidation_Returns400WithFieldErrors() throws Exception {
        // given
        BindingResult bindingResult = mock(BindingResult.class);
        FieldError fieldError = new FieldError("object", "email", "不可為空");
        when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError));

        // 使用真實的 MethodParameter
        MethodParameter methodParameter = new MethodParameter(
                GlobalExceptionHandlerTest.class.getDeclaredMethod("handleValidation_Returns400WithFieldErrors"), -1);
        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(methodParameter, bindingResult);

        // when
        ResponseEntity<Map<String, Object>> response = handler.handleValidation(ex);

        // then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().get("message").toString().contains("不可為空"));
    }

    @Test
    @DisplayName("AuthorizationDeniedException 回傳 403")
    void handleAccessDenied_Returns403() {
        // given
        AuthorizationDeniedException ex = new AuthorizationDeniedException("Access Denied");

        // when
        ResponseEntity<Map<String, Object>> response = handler.handleAccessDenied(ex);

        // then
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals("存取被拒絕", response.getBody().get("message"));
    }

    @Test
    @DisplayName("IllegalStateException 回傳 409")
    void handleIllegalState_Returns409() {
        // given
        IllegalStateException ex = new IllegalStateException("狀態衝突");

        // when
        ResponseEntity<Map<String, Object>> response = handler.handleIllegalState(ex);

        // then
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("狀態衝突", response.getBody().get("message"));
    }

    @Test
    @DisplayName("MissingServletRequestParameterException 回傳 400")
    void handleMissingParam_Returns400() throws Exception {
        // given
        MissingServletRequestParameterException ex =
                new MissingServletRequestParameterException("date", "LocalDate");

        // when
        ResponseEntity<Map<String, Object>> response = handler.handleMissingParam(ex);

        // then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().get("message").toString().contains("date"));
    }

    @Test
    @DisplayName("HttpMessageNotReadableException 回傳 400")
    void handleNotReadable_Returns400() {
        // given
        HttpMessageNotReadableException ex = new HttpMessageNotReadableException("JSON parse error");

        // when
        ResponseEntity<Map<String, Object>> response = handler.handleNotReadable(ex);

        // then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("請求格式錯誤，無法解析", response.getBody().get("message"));
    }

    @Test
    @DisplayName("MethodArgumentTypeMismatchException 回傳 400")
    void handleTypeMismatch_Returns400() {
        // given
        MethodArgumentTypeMismatchException ex = mock(MethodArgumentTypeMismatchException.class);
        when(ex.getName()).thenReturn("date");
        when(ex.getMessage()).thenReturn("type mismatch");

        // when
        ResponseEntity<Map<String, Object>> response = handler.handleTypeMismatch(ex);

        // then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().get("message").toString().contains("date"));
    }

    @Test
    @DisplayName("HttpRequestMethodNotSupportedException 回傳 405")
    void handleMethodNotSupported_Returns405() {
        // given
        HttpRequestMethodNotSupportedException ex = new HttpRequestMethodNotSupportedException("DELETE");

        // when
        ResponseEntity<Map<String, Object>> response = handler.handleMethodNotSupported(ex);

        // then
        assertEquals(HttpStatus.METHOD_NOT_ALLOWED, response.getStatusCode());
    }

    @Test
    @DisplayName("一般 Exception 回傳 500")
    void handleGeneral_Returns500() {
        // given
        Exception ex = new Exception("未預期錯誤");

        // when
        ResponseEntity<Map<String, Object>> response = handler.handleGeneral(ex);

        // then
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("伺服器內部錯誤", response.getBody().get("message"));
    }
}
