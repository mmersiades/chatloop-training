package com.chatloop.training.service.impl;

import com.chatloop.training.CleanDbBeforeTests;
import com.chatloop.training.LocalTestConfig;
import com.chatloop.training.model.content.UserProgram;
import com.chatloop.training.service.CdnService;
import org.junit.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {LocalTestConfig.class}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@CleanDbBeforeTests
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
