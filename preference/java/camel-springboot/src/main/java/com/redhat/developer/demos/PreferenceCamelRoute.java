package com.redhat.developer.demos;

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.http.common.HttpOperationFailedException;
import org.apache.camel.model.rest.RestBindingMode;
import org.springframework.stereotype.Component;

@Component
public class PreferenceCamelRoute extends RouteBuilder {

    private static final String RESPONSE_STRING_FORMAT = "preference => %s\n";


    @Override
    public void configure() throws Exception {
        restConfiguration()
                .component("servlet")
                .enableCORS(true)
                .contextPath("/")
                .bindingMode(RestBindingMode.auto);

        rest("/").get().produces("text/plain")
                .route().routeId("root")
                .to("http4:recommendation:8080/?httpClient.connectTimeout=1000&bridgeEndpoint=true&copyHeaders=true&connectionClose=true")
                .routeId("recommendation")
                .onException(HttpOperationFailedException.class)
                    .handled(true)
                    .process(this::handleHttpFailure)
                    .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(503))
                    .end()
                .onException(Exception.class)
                    .handled(true)
                    .transform(simpleF(RESPONSE_STRING_FORMAT, exceptionMessage()) )
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
}
