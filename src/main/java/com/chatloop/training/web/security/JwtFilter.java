package com.chatloop.training.web.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

//@Component
@Slf4j
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    private AuthenticationExtractor authenticationExtractor;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {

        String jwtToken = request.getHeader(SecurityConstants.AUTHORIZATION_HEADER);
        if(jwtToken == null || jwtToken.isEmpty()) {
            throw new IllegalArgumentException("JWT token is missing");
        }
        Authentication auth = authenticationExtractor.getUserId(jwtToken);

        SecurityContext sc = SecurityContextHolder.getContext();
        sc.setAuthentication(auth);
//        HttpSession session = request.getSession(true);
//        session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, sc);

        filterChain.doFilter(request, response);
    }
}
