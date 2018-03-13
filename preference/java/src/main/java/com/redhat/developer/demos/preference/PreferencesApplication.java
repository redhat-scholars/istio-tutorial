package com.redhat.developer.demos.preference;

import com.uber.jaeger.Tracer;
import com.uber.jaeger.propagation.B3TextMapCodec;
import com.uber.jaeger.reporters.RemoteReporter;
import com.uber.jaeger.senders.HttpSender;
import io.opentracing.propagation.Format.Builtin;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;


@SpringBootApplication
public class PreferencesApplication extends WebMvcConfigurerAdapter {

	public static void main(String[] args) {
		SpringApplication.run(PreferencesApplication.class, args);
	}

	@Bean
	public RestTemplate restTemplate(RestTemplateBuilder restTemplateBuilder) {
		return restTemplateBuilder.build();
	}

	@Bean
	public io.opentracing.Tracer tracer() {
		return new Tracer.Builder("preferences")
				.withExpandExceptionLogs()
				.registerExtractor(Builtin.HTTP_HEADERS, new B3TextMapCodec())
				.registerInjector(Builtin.HTTP_HEADERS, new B3TextMapCodec())
				.withReporter(new RemoteReporter.Builder()
						.withSender(new HttpSender("http://jaeger-collector.istio-system.svc:14268/api/traces"))
						.build())
				.build();
	}
}
