package com.example.preferences;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class PreferencesController {
    RestTemplate restTemplate = new RestTemplate();
    
    @RequestMapping("/")
    public String getPreferences() {
        String url = "http://recommendations:8080/";
        String responseBody;

        try {
            ResponseEntity<String> response
            = restTemplate.getForEntity(url, String.class);
            responseBody = response.getBody();
        } catch (Exception e) {            
            responseBody = e.getMessage();
        }
        
        System.out.println(responseBody);


        return "{\"P1\":\"Red\", \"P2\":\"Big\"} && " + responseBody;
    }
}