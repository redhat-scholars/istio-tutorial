package com.example.recommendations;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.http.HttpStatus;

@RestController
public class RecommendationsController {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private int count = 0;

    private boolean misbehave = false;
    
    @RequestMapping("/")
    public ResponseEntity<String> getRecommendations() {
        count++;
        logger.debug(String.format("Big Red Dog v1 %s", count));

        /* begin timeout and/or circuit-breaker example
        try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {			
			logger.info("Thread interrupted");
		}
		logger.debug("recommendations ready to return");
        // end circuit-breaker example */
        /* inject some poor behavior
        if (misbehave) {
            count = 0;
            misbehave = false;
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body("Ain't Misbehaving");
        }
        // */
        return ResponseEntity.ok(String.format("Clifford v1 %s", count));
        
    }

    @RequestMapping("/misbehave")
    public ResponseEntity<String> misbehave() {
        this.misbehave = true;
        logger.debug("'misbehave' has been set to 'true'");
        return ResponseEntity.ok("Next request to / will return a 503");
    }
    
}