package org.chatloop.user_training.web.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Optional;

import static org.chatloop.user_training.web.security.SecurityConstants.BEARER_PREFIX;

/**
 * Verifies JWT token and extracts from it the user name (subject). If token is
 * valid, we assume user is allowed to proceed since it has been authenticated
 * before on NKK monolith.
 *
 * User ID is used in other parts of the system, including as column in database.
 *
 * Monolith uses claim user_id to propagate info, rather than subject.
 */
@Component
@Slf4j
public class AuthenticationExtractor {

    @Value("${security.jwt.mac.secret}")
    private String jwtMacSecret;

    @PostConstruct
    public void init() {
        assertValid();
    }

    /**
     * Returns empty in case token is invalid.
     */
    public Optional<Authentication> getUserId(String jwt) {
        try {
            DecodedJWT decodedToken = parseJwt(jwt, jwtMacSecret);

            String subject = decodedToken.getSubject();
            Claim claim = decodedToken.getClaim(USER_ID_CLAIM_NAME);
            if (claim == null || claim.isNull()) {
                String message = "JWT token is missing user_id claim for subject " + subject;
                log.error(message);
                throw new RuntimeException(message);
            }
            Integer userId = claim.asInt();
            log.info("Extracted subject {}, user ID {} from JWT token", subject, userId);

            // token is valid, we neither need nor have access to password/credentials
            return Optional.of(new UsernamePasswordAuthenticationToken(userId, null));

        } catch (Exception ex) {
            log.error("Invalid token " + jwt, ex); // token is invalid, so log it in case we need to troubleshoot
            return Optional.empty();
        }
    }

    public static DecodedJWT parseJwt(String jwt, String jwtMacSecret) {
        return JWT.require(Algorithm.HMAC512(jwtMacSecret.getBytes()))
                .build()
                .verify(jwt.replace(BEARER_PREFIX, ""));
    }

    private void assertValid() {
        final int length = jwtMacSecret == null ? 0 : jwtMacSecret.length();
        log.info("Secret JWT key length is {}", length);
        if (length == 0) {
            String message = "JWT secret is either null or empty string, check JWT_SECRET_KEY env variable or security.jwt.mac.secret property";
            log.error(message);
            throw new IllegalStateException(message);
        }
    }

    /**
     * Must be in-sync with nkk monolith.
     */
    public static final String USER_ID_CLAIM_NAME = "user_id";
}
