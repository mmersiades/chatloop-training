package com.chatloop.training.repository;

import com.chatloop.training.CleanDbBeforeTests;
import com.chatloop.training.TestUtil;
import com.chatloop.training.model.event.TrainingEvent;
import com.chatloop.training.model.event.TrainingEventType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@DataJpaTest
@CleanDbBeforeTests
public class TrainingHistoryRepositoryTest {

    @Autowired
    private TrainingHistoryRepository repository;

    @Test
    public void testEventInsertQuery() {
        List<TrainingEvent> events = new ArrayList<>();
        for(TrainingEventType type : TrainingEventType.values()) {
            TrainingEvent event = new TrainingEvent();
            event.setUserId(TestUtil.randStr());
            event.setCourseId(TestUtil.randStr());
            event.setModuleId(TestUtil.randStr());
            event.setTrainingEventType(type);

            event = repository.save(event);
            events.add(event);
        }
        for(TrainingEvent original : events) {
            TrainingEvent queried = repository.findById(original.getId()).get();
            TestUtil.assertTrainingEventEqual(original, queried);
        }
    }
}
