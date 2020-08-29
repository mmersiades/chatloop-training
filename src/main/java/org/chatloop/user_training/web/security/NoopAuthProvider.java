package org.chatloop.user_training.web.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

@Slf4j
public class NoopAuthProvider implements AuthenticationProvider {

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        log.trace("authenticate() {}", authentication);
        return authentication;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        log.trace("supports() {}", authentication);
        return true;
    }
}
