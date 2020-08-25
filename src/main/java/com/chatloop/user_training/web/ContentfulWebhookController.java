package com.chatloop.user_training.web;

import com.chatloop.user_training.service.CdnService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Used to receive webhook calls from Contentful when content model has
 * been changed on their side. Body/payload is JSON. See Postman example.
 */
@RestController
@RequestMapping("webhook")
public class ContentfulWebhookController {

    @Autowired
    private final CdnService cdnService;

    public ContentfulWebhookController(CdnService cdnService) {
        this.cdnService = cdnService;
    }

    /**
     * List of headers that Contentful will provide. See example header/payload JSON
     * files in docs/cful dir. Also available in Contentful activity log.
     */
    interface Headers {
        String HOOK_NAME  = "x-contentful-webhook-name";
        String TOPIC_NAME = "x-contentful-topic";
    }

    @PostMapping
    public void processIncomingHook(@RequestHeader(value = Headers.HOOK_NAME) String hookName,
                                    @RequestHeader(value = Headers.TOPIC_NAME) String topicName,
                                    @RequestBody Map<String, Object> payload) throws Exception {

        log.info("Received call from webhook {}, topic {}", hookName, topicName);
        log.info("Payload " + payload); // TODO handling of JSON payload if we really need it

        cdnService.markCacheStale();
    }

    private final Logger log = LoggerFactory.getLogger(ContentfulWebhookController.class);
}
