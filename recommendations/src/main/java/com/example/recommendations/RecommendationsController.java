package com.example.recommendations;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RecommendationsController {
    
    @RequestMapping("/")
    public String getRecommendations() {
        
        System.out.println("Big Red Dog");

        return "Clifford";
    }
}