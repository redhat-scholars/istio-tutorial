package com.example.recommendations;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

@RestController
public class RecommendationsController {
    
    @RequestMapping("/")
    public String getRecommendations() {
        
        System.out.println("Big Red Dog v1");

        /* begin timeout and/or circuit-breaker example
        try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {			
			e.printStackTrace();
		}
        System.out.println("recommendations ready to return");
        // end circuit-breaker example */
        return "Clifford v1";
    }
    /*
    @RequestMapping("/serviceunavailable")
    public ResponseEntity<String> hello() {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
    }
    */
}