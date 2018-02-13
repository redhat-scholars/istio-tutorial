package com.redhat.developer.demos.recommendation;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

public class RecommendationVerticleTest {

    @Test
    public void parseContainerIdFromHostname() {
        assertThat(RecommendationVerticle.parseContainerIdFromHostname("recommendation-v1-abcdef"), equalTo("abcdef"));
        assertThat(RecommendationVerticle.parseContainerIdFromHostname("recommendation-v2-abcdef"), equalTo("abcdef"));
        assertThat(RecommendationVerticle.parseContainerIdFromHostname("recommendation-v10-abcdef"), equalTo("abcdef"));
        assertThat(RecommendationVerticle.parseContainerIdFromHostname("unknown"), equalTo("unknown"));
        assertThat(RecommendationVerticle.parseContainerIdFromHostname("localhost"), equalTo("localhost"));
    }

}
