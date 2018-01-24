package com.example.customer;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@RestController
public class CustomerController {

    private static final String REMOTE_URL = "http://preferences:8080";

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
