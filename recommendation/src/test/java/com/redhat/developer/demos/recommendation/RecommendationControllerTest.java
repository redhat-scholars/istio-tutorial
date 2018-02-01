package com.redhat.developer.demos.recommendation;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.*;

public class RecommendationControllerTest {

    @Test
    public void parseContainerIdFromHostname() {
        assertThat(RecommendationController.parseContainerIdFromHostname("recommendation-v1-abcdef"), equalTo("abcdef"));
        assertThat(RecommendationController.parseContainerIdFromHostname("recommendation-v2-abcdef"), equalTo("abcdef"));
        assertThat(RecommendationController.parseContainerIdFromHostname("recommendation-v10-abcdef"), equalTo("abcdef"));
        assertThat(RecommendationController.parseContainerIdFromHostname("unknown"), equalTo("unknown"));
        assertThat(RecommendationController.parseContainerIdFromHostname("localhost"), equalTo("localhost"));
    }

}