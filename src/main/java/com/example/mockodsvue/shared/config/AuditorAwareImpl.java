package com.example.mockodsvue.shared.config;

import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * 提供當前使用者資訊給 JPA Auditing
 */
@Component
public class AuditorAwareImpl implements AuditorAware<String> {

    @Override
    public Optional<String> getCurrentAuditor() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return Optional.of("SYSTEM");
        }

        String username = authentication.getName();

        // 匿名使用者
        if ("anonymousUser".equals(username)) {
            return Optional.of("SYSTEM");
        }

        return Optional.of(username);
    }
}
