package com.chatloop.user_training.service.impl;

import com.chatloop.user_training.LocalTestConfig;
import com.chatloop.user_training.model.content.UserProgram;
import com.chatloop.user_training.service.CdnService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {LocalTestConfig.class}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Sql(scripts = "classpath:db_scripts/delete_all.sql")
public class FakeCdnServiceTest {

    @Autowired
    private CdnService cdnService;

    @Test
    public void testBasic() {
        Optional<UserProgram> program = cdnService.fetchProgramById("pro1");
        assertTrue(program.isPresent());
        assertEquals("pro1", program.get().getId());

        program = cdnService.fetchProgramById("pro3");
        assertTrue(program.isPresent());
        assertEquals("pro3", program.get().getId());
    }
}
