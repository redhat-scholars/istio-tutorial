package com.redhat.developer.demos.preference;

import io.micronaut.http.HttpHeaders;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.client.RxHttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.exceptions.HttpClientException;
import javax.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller("/")
public class PreferenceController {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private static final String RESPONSE_STRING_FORMAT = "preference => %s\n";

    @Client("http://recommendation:8080") @Inject
    RxHttpClient client;

    @Get(produces = MediaType.TEXT_PLAIN)
    public HttpResponse<?> getRecommendation(final HttpHeaders headers) {

        try {
            final HttpRequest<?> req = HttpRequest.GET("/")
                .header("baggage-user-agent", headers.findFirst("baggage-user-agent").orElse(""));
            final String result = client.toBlocking().retrieve(req);
            return HttpResponse.ok(String.format(RESPONSE_STRING_FORMAT, result.trim()));
        } catch (HttpClientException ex) {
            logger.warn("Exception trying to get the response from recommendation service.", ex);
            return HttpResponse.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(String.format(RESPONSE_STRING_FORMAT, ex.getMessage()));
        }
    }

    @Get(value="/health")
    public HttpResponse<?> health() {
        return HttpResponse.ok();
    }
}
