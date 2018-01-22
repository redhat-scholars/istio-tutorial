package com.example.customer;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

// These for adding the tracing headers
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpEntity;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;

import java.net.UnknownHostException;

@RestController
public class CustomerController {

    private static final String REMOTE_URL = "http://localhost:8081";

    private static final String RESPONSE_STRING_FORMAT = "C100 * %s *";

    private final RestTemplate restTemplate;

    public CustomerController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @RequestMapping("/")
    public ResponseEntity<String> getCustomer() {
        try {
            String response = restTemplate.getForObject(REMOTE_URL, String.class);
            return ResponseEntity.ok(String.format(RESPONSE_STRING_FORMAT, response));
        } catch (RestClientException ex) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(String.format(RESPONSE_STRING_FORMAT, ex.getCause()));
        }
    }

}