package com.example.customer;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class CustomerController {
    RestTemplate restTemplate = new RestTemplate();
    
    @RequestMapping("/")
    public String getCustomer() {
        String url = "http://preferences:8080/";
        String responseBody;

        try {
            ResponseEntity<String> response
            = restTemplate.getForEntity(url, String.class);
            responseBody = response.getBody();
        } catch (Exception e) {
            responseBody = e.getMessage();
        }
        
        System.out.println(responseBody);

        return "C100 *" + responseBody + " * ";
    }
}