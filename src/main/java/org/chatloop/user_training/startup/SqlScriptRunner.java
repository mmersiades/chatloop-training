package org.chatloop.user_training.startup;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class SqlScriptRunner {

    private final JdbcTemplate jdbcTemplate;

    public void runScript(String scriptPath) throws IOException {
        jdbcTemplate.execute(getSql(scriptPath));
    }

    private String getSql(String filePath) throws IOException {

        try (InputStream inputStream = getClass().getResourceAsStream(filePath)) {
            if (inputStream != null) {
                BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
                return br.lines().collect(Collectors.joining(System.lineSeparator()));
            } else {
                throw new IOException("Sql file not found");
            }
        }

    }
}
