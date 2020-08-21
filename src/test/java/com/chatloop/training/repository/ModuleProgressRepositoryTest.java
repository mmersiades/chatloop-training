package com.chatloop.training.repository;

import com.chatloop.training.CleanDbBeforeTests;
import com.chatloop.training.model.progress.ModuleProgress;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import static com.chatloop.training.TestUtil.assertModuleProgressEqual;
import static com.chatloop.training.data.DataGen.getModuleProgress;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@DataJpaTest
@CleanDbBeforeTests
public class ModuleProgressRepositoryTest {

    @Autowired
    private ModuleProgressRepository repository;

    @Test
    public void testModuleProgressInsertQuery() {
        ModuleProgress moduleProgress = getModuleProgress();

        moduleProgress = repository.save(moduleProgress);

        ModuleProgress found = repository.findById(moduleProgress.getId()).get();

        assertModuleProgressEqual(moduleProgress, found);
    }
}
