package com.example.recommendations;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.HttpStatus;

@RestController
public class RecommendationsController {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * Counter to help us see the lifecycle
     */
    private int count = 0;

    /**
     * Flag for throwing a 503 when enabled
     */
    private boolean misbehave = false;

    private static final String HOSTNAME =
            parseContainerIdFromHostname(System.getenv().getOrDefault("HOSTNAME", "unknown"));

    static String parseContainerIdFromHostname(String hostname) {
        return hostname.replaceAll("recommendations-v\\d+-", "");
    }

    @RequestMapping("/")
    public ResponseEntity<String> getRecommendations() {
        count++;
        logger.debug(String.format("Big Red Dog v1 %s %d", HOSTNAME, count));

        //timeout();

        logger.debug("recommendations ready to return");
        if (misbehave) {
            return doMisbehavior();
        }
        return ResponseEntity.ok(String.format("Clifford v1 %s %d", HOSTNAME, count));
    }

    private void timeout() {
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            logger.info("Thread interrupted");
        }
    }

    private ResponseEntity<String> doMisbehavior() {
        count = 0;
        misbehave = false;
        logger.debug(String.format("Misbehaving %d", count));
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body("D'oh");
    }

    @RequestMapping("/misbehave")
    public ResponseEntity<String> flagMisbehave() {
        this.misbehave = true;
        logger.debug("'flagMisbehave' has been set to 'true'");
        return ResponseEntity.ok("Next request to / will return a 503");
    }

}