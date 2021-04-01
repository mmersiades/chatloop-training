package org.chatloop.user_training.web.impl;

import lombok.extern.slf4j.Slf4j;
import org.chatloop.user_training.model.progress.QualificationChangeSummary;
import org.chatloop.user_training.service.ApplicationSecretsService;
import org.chatloop.user_training.web.ChatloopMonolithFacade;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicInteger;

import static java.util.stream.Collectors.groupingBy;

/**
 * @inheritDoc
 */
@Slf4j
public class ChatloopMonolithFacadeImpl implements ChatloopMonolithFacade {

    private static final int CHUNK_SIZE = 250;

    @Value("${chatloop.monolith.rest.endpoint}")
    private String restEndpoint;

    private final ApplicationSecretsService applicationSecretsService;

    private final RestTemplate restTemplate = new RestTemplate();

    public ChatloopMonolithFacadeImpl(ApplicationSecretsService applicationSecretsService) {
        this.applicationSecretsService = applicationSecretsService;
    }

    @Override
    public void notifyEarnedQualificationsChanges(QualificationChangeSummary summary) {
        log.debug("Received a QualificationChangeSummary. removals: {}, changes: {}", summary.getUsersToReset().size(), summary.getChanges().size());

        var chunks = chunked(summary);
        log.debug("Chunked QualificationChangeSummary into {} chunks.", chunks.size());

        for (QualificationChangeSummary chunk : chunks) {
            log.debug("Sending chunk downstream. removals: {}, changes: {}", chunk.getUsersToReset().size(), chunk.getChanges().size());

            Integer response = sendChangeSummaryRequest(chunk);
            log.debug("Downstream responded with {}", response);
        }
    }

    private Integer sendChangeSummaryRequest(QualificationChangeSummary summary) {
        String url = String.format("%s/qualifications", restEndpoint);
        log.debug("About to send PUT request against: {}", url);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", String.format("Bearer %s", applicationSecretsService.monolithRestServiceJWT()));

        HttpEntity<QualificationChangeSummary> request = new HttpEntity<>(summary, headers);
        ResponseEntity<Integer> response = makeRestCall(url, request);
        return response.getBody();
    }

    protected ResponseEntity<Integer> makeRestCall(String url, HttpEntity<QualificationChangeSummary> request) {
        return restTemplate.exchange(url, HttpMethod.PUT, request, Integer.class);
    }

    /**
     * Given a {@link QualificationChangeSummary}, this functions chunks the totalSummary so that the
     * {@code removals} go into a new {@link QualificationChangeSummary} alone and all {@code changes}
     * are split into batches of 250 items before they go into a new {@link QualificationChangeSummary}
     * as well.
     *
     * Example:
     * input QualificationChangeSummary(removals: 450 items, changes: 300 items)
     * output:
     *   - QualificationChangeSummary(removals: 450 items, changes: 0 items)
     *   - QualificationChangeSummary(removals: 0 items, changes: 250 items)
     *   - QualificationChangeSummary(removals: 0 items, changes: 50 items)
     *
     * @param totalSummary a total {@link QualificationChangeSummary}
     * @return Collection<QualificationChangeSummary>
     */
    private Collection<QualificationChangeSummary> chunked(QualificationChangeSummary totalSummary) {
        var counter = new AtomicInteger();
        var removals = totalSummary.getUsersToReset();
        var changes = totalSummary.getChanges()
                .stream()
                .collect(groupingBy(x -> counter.getAndIncrement() / CHUNK_SIZE));

        var summaries = new ArrayList<QualificationChangeSummary>(1 + changes.size());
        summaries.add(new QualificationChangeSummary(removals, Collections.emptyList()));
        for (Integer i : changes.keySet()) {
            var chunk = changes.get(i);
            summaries.add(new QualificationChangeSummary(Collections.emptyList(), chunk));
        }

        return summaries;
    }
}
