package com.redhat.developer.demos;

import io.opentracing.Span;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.http.common.HttpOperationFailedException;
import org.apache.camel.model.rest.RestBindingMode;
import org.apache.camel.opentracing.ActiveSpanManager;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

@Component("CustomerCamelRoute")
public class CustomerCamelRoute extends RouteBuilder {

    private static final String RESPONSE_STRING_FORMAT = "customer => %s\n";

    @Override
    public void configure() throws Exception {
        restConfiguration()
                .component("servlet")
                .enableCORS(true)
                .contextPath("/")
                .bindingMode(RestBindingMode.auto);

        rest("/").get().consumes(MediaType.TEXT_PLAIN_VALUE)
                .route().routeId("root")
                .pipeline()
                    .bean("CustomerCamelRoute", "addTracer")
                    .to("http4:preference:8080/?httpClient.connectTimeout=1000&bridgeEndpoint=true&copyHeaders=true&connectionClose=true")
                .end()
                .convertBodyTo(String.class)
                .onException(HttpOperationFailedException.class)
                    .handled(true)
                    .process(this::handleHttpFailure)
                    .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(503))
                .end()
                .onException(Exception.class)
                    .log(exceptionMessage().toString())
                    .handled(true)
                    .transform(simpleF(RESPONSE_STRING_FORMAT, exceptionMessage()))
                    .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(503))
                .end()
                .transform(simpleF(RESPONSE_STRING_FORMAT, "${body}"))
        .endRest();
    }

    private void handleHttpFailure(Exchange exchange) {
        HttpOperationFailedException e = exchange.getProperty(Exchange.EXCEPTION_CAUGHT, HttpOperationFailedException.class);
        exchange.getOut().setHeaders(exchange.getIn().getHeaders());
        exchange.getOut().setBody(String.format(RESPONSE_STRING_FORMAT,
                String.format("%d %s", e.getStatusCode(), e.getResponseBody())
        ));
    }

    public void addTracer(Exchange exchange){
        String userAgent = (String) exchange.getIn().getHeader("user-agent");
        Span span = ActiveSpanManager.getSpan(exchange);
        span.setBaggageItem("user-agent",userAgent);
    }
}
