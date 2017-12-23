package com.example.recommendations;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
public class RecommendationsController {
    int cnt = 0;
    boolean misbehave = false;
    
    @RequestMapping("/")
    public String getRecommendations() {
        
        cnt ++;
        System.out.println("Big Red Dog v1 " + cnt);
        
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
            cnt = 0;
            misbehave = false;
            throw new ServiceUnavailableException();            
        } 
        // */       
        return "Clifford v1 " + cnt;
        
    }

    @RequestMapping("/misbehave")
    public HttpStatus misbehave() {
        this.misbehave = true;
        return HttpStatus.OK;
    }
    
}


@ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
class ServiceUnavailableException extends RuntimeException {

}