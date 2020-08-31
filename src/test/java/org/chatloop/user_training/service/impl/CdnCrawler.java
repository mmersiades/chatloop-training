package org.chatloop.user_training.service.impl;

import com.contentful.java.cda.CDAContentType;
import com.contentful.java.cda.CDAEntry;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.chatloop.user_training.IntegrationTestConfig;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Used to scan Contentful and export CSV file with course/module pairs which
 * are needed/used for migration from Monolith. Comment out Ignore annotation when
 * running. Also, change PATH to any directory of your preference. Change space/env
 * IDs if needed in application-test.properties to desired values.
 */
@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {IntegrationTestConfig.class}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Slf4j
@Ignore // comment it out when running
public class CdnCrawler {

    @Value("${contentful.spaceId}")
    private String spaceId;

    @Value("${contentful.envId}")
    private String envId;
    @Autowired
    private StatefulCdnService cdnService;

    // change this to location of your choice
    private static final String PATH = "./";

    @Test
    public void testBasic() throws Exception {
        List<Tuple> tuples = new ArrayList<>();
        for (CDAEntry entry : cdnService.getSyncedSpace().entries().values()) {
            CDAContentType cdnType = entry.contentType();
            if (cdnType.name().equals("course")) {
                String courseId = entry.id();
                List<CDAEntry> modules = entry.getField("modules");
                for (CDAEntry moduleEntry : modules) {
                    tuples.add(new Tuple(courseId, moduleEntry.id()));
                }
            }
        }
        log.info("Found {} courses/module pairs", tuples.size());

        for (Tuple t : tuples) {
            log.info(t.toStr());
        }
        String header = "space_id,env_id,course_id,module_id\n";
        String text = header + tuples.stream().map(Tuple::toStr).collect(Collectors.joining("\n"));
        Files.write(Paths.get(PATH + "contentful-data.csv"), text.getBytes());
    }

    @RequiredArgsConstructor
    @ToString
    @SuppressWarnings("checkstyle:VisibilityModifier")
    private final class Tuple {

        public final String courseId;
        public final String moduleId;

        String toStr() {
            return String.join(",", Arrays.asList(spaceId, envId, courseId, moduleId));
        }
    }
}
