package com.example.githubmiddleman;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpEntity;
import javax.servlet.http.HttpServletRequest;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

@RestController
public class GithubmiddlemanController {
   
  
   @RequestMapping("/") 
   public String users() {
        RestTemplate restTemplate = new RestTemplate();
        String url = "http://api.github.com:443/users";

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
   } // users
}