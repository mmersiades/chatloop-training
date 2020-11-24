package org.chatloop.user_training.service.impl;

import org.chatloop.user_training.IntegrationTestConfig;
import org.chatloop.user_training.LocalTestConfig;
import org.chatloop.user_training.model.content.UserProgram;
import org.chatloop.user_training.service.CdnService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@SpringBootTest(classes = { IntegrationTestConfig.class }, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Sql(scripts = "classpath:db_scripts/delete_all.sql")
public class StatefulCdnServiceStaleCacheTest {

    @Autowired
    private StatefulCdnService cdnService;

    @Test
    public void testMarkCacheStale() {
        List<UserProgram> programs = cdnService.fetchUserPrograms();
        assertFalse(programs.isEmpty());

        cdnService.markCacheStale();

        List<UserProgram> programs2 = cdnService.fetchUserPrograms();
        assertFalse(programs.isEmpty());

        assertEquals(programs.size(), programs2.size());
    }
}
