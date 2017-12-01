package com.example.customer;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

// These for adding the tracing headers
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpEntity;
import javax.servlet.http.HttpServletRequest;

@RestController
public class CustomerController {
    RestTemplate restTemplate = new RestTemplate();
    
    @RequestMapping("/")
    public String getCustomer(HttpServletRequest request) {
        String url = "http://preferences/";
        // add the tracing headers
        HttpHeaders httpHeaders = new HttpHeaders();
        String headerValue = request.getHeader("x-request-id");
        httpHeaders.add("x-request-id", headerValue);
        headerValue = request.getHeader("x-b3-traceid");            
        httpHeaders.add("x-b3-traceid", headerValue);
        headerValue = request.getHeader("x-b3-spanid");            
        httpHeaders.add("x-b3-spanid", headerValue);
        headerValue = request.getHeader("x-b3-parentspanid");            
        httpHeaders.add("x-b3-parentspanid", headerValue);
        headerValue = request.getHeader("x-b3-sampled");            
        httpHeaders.add("x-b3-sampled", headerValue);
        headerValue = request.getHeader("x-b3-flags");            
        httpHeaders.add("x-b3-flags", headerValue);
        headerValue = request.getHeader("x-ot-span-context");            
        httpHeaders.add("x-ot-span-context", headerValue);
        HttpEntity<String> httpEntity = new HttpEntity<>("", httpHeaders);
        // end adding the tracing headers
        
        String responseBody;
        

        try {
            ResponseEntity<String> response
            = restTemplate.exchange(url, HttpMethod.GET, httpEntity, String.class);
            responseBody = response.getBody();
        } catch (Exception e) {
            responseBody = e.getMessage();
        }
        
        System.out.println(responseBody);

        return "C100 *" + responseBody + " * ";
    }
}