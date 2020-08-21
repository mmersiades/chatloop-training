package com.chatloop.training.web.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import static com.chatloop.training.web.security.SecurityConstants.BEARER_PREFIX;

//@Component
@Slf4j
public class AuthenticationExtractor {

    @Value("${security.jwt.mac.secret}")
    private String jwtMacSecret;

    /**
     * This method gets a jwt token and returns user ID.
     */
    public Authentication getUserId(String jwt) {
        try {
            DecodedJWT decodedToken = parseJwt(jwt, jwtMacSecret);

            String userId = decodedToken.getSubject();
            log.trace("Extracted user {} from JWT token {}", userId, jwt);

            return new UsernamePasswordAuthenticationToken(userId, null);

        } catch (TokenExpiredException ex) {
            throw new JWTVerificationException("Token Expired: " + jwt, ex);
        }
    }

    public static DecodedJWT parseJwt(String jwt, String jwtMacSecret) {
        return JWT.require(Algorithm.HMAC512(jwtMacSecret.getBytes()))
                .build()
                .verify(jwt.replace(BEARER_PREFIX, ""));
    }
}
