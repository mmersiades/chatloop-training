package com.chatloop.user_training.web.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.Optional;

/**
 * Returns 401 if token is missing or invalid.. Otherwise, if present and valid, extracts
 * user ID from token in form of Authentication object and propagates it via security
 * context.
 */
@Slf4j
public class JwtFilter extends BasicAuthenticationFilter {

    private final AuthenticationExtractor authenticationExtractor;

    public JwtFilter(AuthenticationManager authManager, AuthenticationExtractor authenticationExtractor) {
        super(authManager);
        this.authenticationExtractor = authenticationExtractor;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws IOException, ServletException {

        String jwtToken = request.getHeader(SecurityConstants.AUTHORIZATION_HEADER);
        if (jwtToken == null || jwtToken.isEmpty()) {
            log.error("JWT token is missing, found headers {}", Collections.list(request.getHeaderNames()));
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        Optional<Authentication> auth = authenticationExtractor.getUserId(jwtToken);
        if (auth.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        log.info("Extracted JWT principal {}", auth.get().getPrincipal());

        // this is then read by TrainingProgressService.getUserId()
        SecurityContext sc = SecurityContextHolder.getContext();
        sc.setAuthentication(auth.get());

        filterChain.doFilter(request, response);
    }

    // leaving it here for now in case it is needed in the future
    //    @Override
//    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
//        String uri = request.getRequestURI();
//        boolean shouldNotFilter = !uri.equals("/graphql");
//        log.trace("JWT shouldNotFilter {} : {}", uri,  shouldNotFilter);
//        return shouldNotFilter;
//    }
}
