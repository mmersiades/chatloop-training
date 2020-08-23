package com.chatloop.training.sec;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;

import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Use this to generate new JWT token which can be used for testing/dev.
 *
 * Feel free to change subject (user name) to any value you prefer.
 */
public class JwtTokenGenerator {

    public static void main(String[] args) throws Exception {
        String secret = "secret";
        String issuer = "ChatloopTrainingTester";
        String subject = "tester1"; // change user name if needed

        Algorithm algorithm = Algorithm.HMAC512(secret);
        String token = JWT.create()
                .withIssuer(issuer)
                .withSubject(subject)
                .withExpiresAt(new GregorianCalendar(2022, Calendar.JANUARY, 1).getTime())
                .sign(algorithm);

        System.out.println(token);
    }
}
