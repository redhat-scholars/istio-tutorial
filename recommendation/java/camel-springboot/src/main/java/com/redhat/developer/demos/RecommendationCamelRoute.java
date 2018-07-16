package com.redhat.developer.demos;

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;
import org.springframework.stereotype.Component;

@Component("RecommendationCamelRoute")
public class RecommendationCamelRoute extends RouteBuilder {

    private static final String RESPONSE_STRING_FORMAT = "recommendation v1 from '%s': %d\n";

    private static final String HOSTNAME = parseContainerIdFromHostname(
            System.getenv().getOrDefault("HOSTNAME", "unknown")
    );

    static String parseContainerIdFromHostname(String hostname) {
        return hostname.replaceAll("recommendation-v\\d+-", "");
    }

    /**
     * Counter to help us see the lifecycle
     */
    private int count = 0;

    /**
     * Flag for throwing a 503 when enabled
     */
    private boolean misbehave = false;


    @Override
    public void configure() throws Exception {
        restConfiguration()
                .component("servlet")
                .enableCORS(true)
                .contextPath("/")
                .bindingMode(RestBindingMode.auto);

        rest("/").get().produces("text/plain")
                .route().routeId("root")
//                .to("direct:timeout")
                .choice()
                    .when(method("RecommendationCamelRoute", "isMisbehave"))
                        .to("direct:misbehave")
                    .otherwise()
                        .to("direct:response")
                .endChoice()
        .endRest();

        rest("/misbehave").get().produces("text/plain")
                .route().routeId("misbehave")
                .process(exchange -> {
                    this.misbehave = true;
                    log.info("'misbehave' has been set to 'true'");
                    exchange.getOut().setBody("Following requests to '/' will return a 503\n");
                })
        .endRest();


        rest("/behave").get().produces("text/plain")
                .route().routeId("behave")
                .process(exchange -> {
                    this.misbehave = false;
                    log.info("'misbehave' has been set to 'false'");
                    exchange.getOut().setBody("Following requests to '/' will return a 200\n");
                })
        .endRest();

        from("direct:response")
                .process(exchange -> {
                   count++;
                   log.info(String.format("recommendation request from %s: %d", HOSTNAME, count));
                   exchange.getOut().setBody(String.format(RESPONSE_STRING_FORMAT, HOSTNAME, count));
                }).end();

        from("direct:misbehave")
                .log(String.format("Misbehaving %d", count))
                .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(503))
                .transform(simpleF("recommendation misbehavior from '%s'\\n", HOSTNAME))
                .process(exchange -> {
                    count = 0;
                }).end();

        from("direct:timeout")
                .process(exchange -> {Thread.sleep(3000);}).end();
    }

    public boolean isMisbehave() {
        return misbehave;
    }
}
