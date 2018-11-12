package com.redhat.developer.demos.recommendation;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller("/")
public class RecommendationController {

    private static final String RESPONSE_STRING_FORMAT = "recommendation v1 from '%s': %d";
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private static final String HOSTNAME = parseContainerIdFromHostname(
        System.getenv().getOrDefault("HOSTNAME", "unknown")
    );

    static String parseContainerIdFromHostname(String hostname) {
        return hostname.replaceAll("recommendation-v\\d+-", "");
    }

    /**
     * Counter to help us see the lifecycle
     */
    private int count = 0;

    /**
     * Flag for enabling timeout
     */
    private boolean timeout = false;

    /**
     * Flag for throwing a 503 when enabled
     */
    private boolean misbehave = false;

    @Get(produces = MediaType.TEXT_PLAIN)
    public HttpResponse<?> getRecommendation() throws InterruptedException {

        logger.info(String.format("recommendation request from %s: %d", HOSTNAME, count));
        if (misbehave) {
            count = 0;
            logger.info(String.format("Misbehaving %d", count));
            return HttpResponse.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(String.format("recommendation misbehavior from '%s'\n", HOSTNAME));
        } else {
            count++;
            if (timeout){
                Thread.sleep(3000);
            }
            return HttpResponse
                .ok(String.format(RESPONSE_STRING_FORMAT, HOSTNAME, count));
        }
    }

    @Get(value="/timeout", produces = MediaType.TEXT_PLAIN)
    public HttpResponse<?> timeout() {
        this.timeout = true;
        return HttpResponse.ok("Timeout enabled");
    }

    @Get(value="/misbehave", produces = MediaType.TEXT_PLAIN)
    public HttpResponse<?> misbehave() {
        this.misbehave = true;
        logger.info("'misbehave' has been set to 'true'");
        return HttpResponse.ok("Following requests to '/' will return a 503\n");
    }

    @Get(value="/behave", produces = MediaType.TEXT_PLAIN)
    public HttpResponse<?> behave() {
        this.misbehave = false;
        logger.info("'misbehave' has been set to 'false'");
        return HttpResponse.ok("Following requests to '/' will return a 200\n");
    }

    @Get(value="/health")
    public HttpResponse<?> health() {
        return HttpResponse.ok();
    }

}
