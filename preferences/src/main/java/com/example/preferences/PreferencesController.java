package com.example.preferences;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;

@RestController
public class PreferencesController {

    @Value("${recommendations.api.url:http://recommendations:8080}")
    private String remoteURL;

    private static final String RESPONSE_STRING_FORMAT = "{\"P1\":\"Red\", \"P2\":\"Big\"} && %s";

    private final RestTemplate restTemplate;

    public PreferencesController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @RequestMapping("/")
    public ResponseEntity<?> getPreferences(HttpServletRequest request) {
        try {
            String responseBody = restTemplate.getForObject(remoteURL, String.class);
            return ResponseEntity.ok(String.format(RESPONSE_STRING_FORMAT, responseBody));

        } catch (RestClientException ex) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body(String.format(RESPONSE_STRING_FORMAT, ex.getCause()));
        }
    }

}