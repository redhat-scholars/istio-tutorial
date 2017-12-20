package com.example.recommendations;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RecommendationsController {
    
    @RequestMapping("/")
    public String getRecommendations() {
        
        System.out.println("Big Red Dog v2");

        // begin circuit-breaker example
        try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {			
			e.printStackTrace();
		}
        System.out.println("recommendations ready to return");
        // end circuit-breaker example
        return "Clifford v2";
    }
}