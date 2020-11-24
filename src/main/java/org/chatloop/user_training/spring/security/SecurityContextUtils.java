package org.chatloop.user_training.spring.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@Slf4j
public class SecurityContextUtils {
    /**
     * Extracts and returns the user id from Spring's security context.
     * @return String
     */
    public static String getUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object principal = authentication.getPrincipal();
        log.info("Extracted user ID {}", principal);
        return String.valueOf(principal);
    }
}
