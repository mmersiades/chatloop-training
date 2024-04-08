package org.chatloop.user_training.web;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.chatloop.user_training.startup.DatabaseCleaner;
import org.chatloop.user_training.startup.DatabaseLoader;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Arrays;

@RestController
@AllArgsConstructor
@Slf4j
public class E2EController {
    public static final String E2E_ROOT_PATH = "/e2e";

    private final Environment env;
    private final DatabaseCleaner databaseCleaner;
    private final DatabaseLoader databaseLoader;

    @PreAuthorize("hasRole('E2E')")
    @PostMapping(E2E_ROOT_PATH)
    public ResponseEntity<String> resetDB() throws IOException {

        boolean validProfile = validateProfile();

        if (validProfile) {
            log.info("Resetting mock data for end-to-end testing");
            databaseCleaner.clean();
            databaseLoader.run();

            return ResponseEntity.status(HttpStatus.OK).body("The db has been reset with fresh mock data");
        }

        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body("This method is not allowed");
    }

    private boolean validateProfile() {
        return Arrays.asList(env.getActiveProfiles()).contains("e2e");
    }
}
