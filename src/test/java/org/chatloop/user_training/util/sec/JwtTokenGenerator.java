package org.chatloop.user_training.util.sec;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;

import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Use this to generate new JWT token which can be used for testing/dev.
 *
 * Feel free to change subject (user name) to any value you prefer.
 *
 * Can be viewed in https://jwt.io/.
 */
public class JwtTokenGenerator {

    public static void main(String[] args) throws Exception {
        String secret = "D*G-KaPdSgUkXp2s5v8y/B?E(H+MbQeThWmYq3t6w9z$C&F)J@NcRfUjXn2r4u7x"; // monolith secret
        String issuer = "ChatloopTrainingTester";

        // or users
        String[] subjects = new String[] {
                "tester",
                "tester0",
                "tester1",
        };
        for (String subject : subjects) {
            Algorithm algorithm = Algorithm.HMAC512(secret);
            String token = JWT.create()
                    .withIssuer(issuer)
                    .withSubject(subject)
                    .withClaim("user_id", 123) // needed
                    .withExpiresAt(new GregorianCalendar(2025, Calendar.JANUARY, 1).getTime())
                    .sign(algorithm);

            System.out.println(subject);
            System.out.println(token);
        }
    }
}
