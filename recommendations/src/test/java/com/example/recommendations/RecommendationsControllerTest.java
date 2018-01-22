package com.example.recommendations;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.*;

public class RecommendationsControllerTest {

    @Test
    public void parseContainerIdFromHostname() {
        assertThat(RecommendationsController.parseContainerIdFromHostname("recommendations-v1-abcdef"), equalTo("abcdef"));
        assertThat(RecommendationsController.parseContainerIdFromHostname("recommendations-v2-abcdef"), equalTo("abcdef"));
        assertThat(RecommendationsController.parseContainerIdFromHostname("recommendations-v10-abcdef"), equalTo("abcdef"));
        assertThat(RecommendationsController.parseContainerIdFromHostname("unknown"), equalTo("unknown"));
        assertThat(RecommendationsController.parseContainerIdFromHostname("localhost"), equalTo("localhost"));
    }

}