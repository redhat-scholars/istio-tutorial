package com.example.httpbinmiddleman;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

@RestController
public class HttpbinmiddlemanController {

    @RequestMapping
    public String headers() {
        RestTemplate restTemplate = new RestTemplate();
        String url = "http://httpbin.org/headers";

        HttpHeaders httpHeaders = new HttpHeaders();
        HttpEntity<String> httpEntity = new HttpEntity<>("", httpHeaders);

        String responseBody;
        try {
            ResponseEntity<String> response
            = restTemplate.exchange(url, HttpMethod.GET, 
                httpEntity,
                String.class);
            responseBody = response.getBody();
        } catch (Exception e) {            
            responseBody = e.getMessage();
        }
            return responseBody + "\n";
    } // headers
        
}