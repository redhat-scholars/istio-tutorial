package com.example.preferences;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

// These for adding the tracing headers
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpEntity;
import javax.servlet.http.HttpServletRequest;
import org.springframework.http.HttpMethod;

import org.springframework.http.HttpStatus;

@RestController
public class PreferencesController {
    RestTemplate restTemplate = new RestTemplate();

    @RequestMapping("/")
    public ResponseEntity<?> getPreferences(HttpServletRequest request) {
        String url = "http://recommendations:8080/";
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
        // end adding the tracing headers
        // if you wish to create routing rules on user-agent 

        String useragent = request.getHeader("user-agent");
        httpHeaders.add("user-agent", useragent);

        HttpEntity<String> httpEntity = new HttpEntity<>("", httpHeaders);

        String responseBody;

        try {
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, httpEntity, String.class);
            return response;
        } catch (Exception e) {
            responseBody = e.getMessage();
            System.out.println("Ex:" + responseBody);
            if (responseBody.contains("java.net.UnknownHostException")
                    || responseBody.contains("Service Unavailable")) {
                throw new ServiceUnavailableException(responseBody);
            } else {
                throw new ServiceException(responseBody);
            }
        }
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    class ServiceException extends RuntimeException {
        public ServiceException(String message) {
            super(message);
        }
    }

    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    class ServiceUnavailableException extends RuntimeException {
        public ServiceUnavailableException(String message) {
            super(message);
        }
    }

}