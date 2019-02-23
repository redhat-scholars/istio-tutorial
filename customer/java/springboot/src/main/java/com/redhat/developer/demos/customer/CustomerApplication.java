package com.redhat.developer.demos.customer;

import io.ap4k.docker.annotation.EnableDockerBuild;
import io.ap4k.kubernetes.annotation.KubernetesApplication;
import io.ap4k.option.annotation.JvmOptions;
import io.ap4k.option.annotation.SecureRandomSource;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@KubernetesApplication(group = "example", version = "v1")
@EnableDockerBuild
@JvmOptions(xms=128, xmx=256, preferIPv4Stack = true, secureRandom = SecureRandomSource.NonBlocking)
@SpringBootApplication
public class CustomerApplication extends WebMvcConfigurerAdapter {

    public static void main(String[] args) {
        SpringApplication.run(CustomerApplication.class, args);
    }

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder restTemplateBuilder) {
        return restTemplateBuilder.build();
    }
}
