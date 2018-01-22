package com.example.preferences;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;

@RestController
public class PreferencesController {

    private static final String REMOTE_URL = "http://recommendations:8080";

    private static final String RESPONSE_STRING_FORMAT = "{\"P1\":\"Red\", \"P2\":\"Big\"} && %s";

    private final RestTemplate restTemplate;

    public PreferencesController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @RequestMapping("/")
    public ResponseEntity<?> getPreferences(HttpServletRequest request) {
        try {
            String responseBody = restTemplate.getForObject(REMOTE_URL, String.class);
            return ResponseEntity.ok(String.format(RESPONSE_STRING_FORMAT, responseBody));

        } catch (RestClientException ex) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(String.format(RESPONSE_STRING_FORMAT, ex.getCause()));
        }
    }

}