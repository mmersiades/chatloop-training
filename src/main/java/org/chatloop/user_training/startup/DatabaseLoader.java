package org.chatloop.user_training.startup;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.io.IOException;


@Slf4j
@Component
@RequiredArgsConstructor
public class DatabaseLoader {
    private final SqlScriptRunner scriptRunner;

    public void run() throws IOException {
        scriptRunner.runScript("/e2e_data.sql");
    }


    @Profile("e2e")
    @Component
    static class Runner implements CommandLineRunner {

        private final DatabaseLoader databaseLoader;

        private final DatabaseCleaner databaseCleaner;

        @Value("${app.mockData.cleanTables:false}")
        private Boolean cleanMockedData;

        @Autowired
        Runner(DatabaseLoader databaseLoader, DatabaseCleaner databaseCleaner) {
            this.databaseLoader = databaseLoader;
            this.databaseCleaner = databaseCleaner;
        }

        @Override
        public void run(String... args) throws IOException {
            if (cleanMockedData) {
                databaseCleaner.clean();
                log.info("E2E test data cleared");
            }

            databaseLoader.run();
            log.info("E2E test data loaded");
        }

    }

}
