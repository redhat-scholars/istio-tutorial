package com.redhat.developer.demos.customer;

import com.redhat.developer.demos.customer.tracing.HttpHeaderForwarderClientHttpRequestInterceptor;
import com.redhat.developer.demos.customer.tracing.HttpHeaderForwarderHandlerInterceptor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.util.Collections;

@SpringBootApplication
public class CustomerApplication extends WebMvcConfigurerAdapter {

    public static void main(String[] args) {
        SpringApplication.run(CustomerApplication.class, args);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new HttpHeaderForwarderHandlerInterceptor());
    }

    @Bean
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setInterceptors(Collections.singletonList(new HttpHeaderForwarderClientHttpRequestInterceptor()));
        return restTemplate;
    }

}
