package com.redhat.developer.demos.recommendation;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

public class MainVerticleTest {

    @Test
    public void parseContainerIdFromHostname() {
        assertThat(MainVerticle.parseContainerIdFromHostname("recommendation-v1-abcdef"), equalTo("abcdef"));
        assertThat(MainVerticle.parseContainerIdFromHostname("recommendation-v2-abcdef"), equalTo("abcdef"));
        assertThat(MainVerticle.parseContainerIdFromHostname("recommendation-v10-abcdef"), equalTo("abcdef"));
        assertThat(MainVerticle.parseContainerIdFromHostname("unknown"), equalTo("unknown"));
        assertThat(MainVerticle.parseContainerIdFromHostname("localhost"), equalTo("localhost"));
    }

}
