package com.example.recommendations;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
public class RecommendationsController {
    int cnt = 0; // helps us see the lifecycle 
    boolean misbehave = false; // a flag for throwing a 503
    final String hostname = System.getenv().getOrDefault("HOSTNAME", "unknown");
    
    @RequestMapping("/")
    public String getRecommendations() {
        
        cnt ++;        
        // hostname.substring(19) to remove "recommendations-v1-"
        System.out.println("Big Red Dog v1 " + hostname.substring(19) + " " + cnt);
        
        /* begin timeout and/or circuit-breaker example 
        try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {			
			e.printStackTrace();
		}
        System.out.println("recommendations ready to return");
        // end circuit-breaker example */
        /* inject some poor behavior
        if (misbehave) {
              misbehave = false;
              cnt = 0;
            System.out.println("Misbehaving " + cnt);
            throw new ServiceUnavailableException("D'oh");
        } 
        // */       
        return "Clifford v1 " + hostname.substring(19) + " " + cnt;
    }

    @RequestMapping("/misbehave")
    public HttpStatus misbehave() {
        this.misbehave = true; // set a flag
        return HttpStatus.OK;
    }
    
}

@ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
class ServiceUnavailableException extends RuntimeException {
    public ServiceUnavailableException(String message) {
        super(message);
    }
}