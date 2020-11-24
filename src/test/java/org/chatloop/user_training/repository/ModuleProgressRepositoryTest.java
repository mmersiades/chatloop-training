package org.chatloop.user_training.repository;

import org.chatloop.user_training.LocalTestConfig;
import org.chatloop.user_training.model.progress.ModuleProgress;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;

import static org.chatloop.user_training.util.TestUtil.assertModuleProgressEqual;
import static org.chatloop.user_training.util.data.DataGen.getModuleProgress;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@DataJpaTest
@ContextConfiguration(classes = LocalTestConfig.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Sql(scripts = "classpath:db_scripts/delete_all.sql")
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
