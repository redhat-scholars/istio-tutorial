package com.redhat.developer.demos.recommendation;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.healthchecks.HealthCheckHandler;
import io.vertx.ext.healthchecks.Status;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;


public class RecommendationVerticle extends AbstractVerticle {

    private static final String RESPONSE_STRING_FORMAT = "recommendation v1 from '%s': %d\n";

    private static final String HOSTNAME =
        parseContainerIdFromHostname(System.getenv().getOrDefault("HOSTNAME", "unknown"));

    static String parseContainerIdFromHostname(String hostname) {
        return hostname.replaceAll("recommendation-v\\d+-", "");
    }

    private final Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * Counter to help us see the lifecycle
     */
    private int count = 0;

    /**
     * Flag for throwing a 503 when enabled
     */
    private boolean misbehave = false;

    @Override
    public void start() throws Exception {
        Router router = Router.router(vertx);
//        router.get("/").handler(this::timeout);
        router.get("/").handler(this::logging);
        router.get("/").handler(this::getRecommendations);
        router.get("/misbehave").handler(this::misbehave);
        router.get("/behave").handler(this::behave);

        HealthCheckHandler hc = HealthCheckHandler.create(vertx);
        hc.register("dummy-health-check", future -> future.complete(Status.OK()));
        router.get("/health").handler(hc);

        vertx.createHttpServer().requestHandler(router::accept).listen(8080);
    }

    private void logging(RoutingContext ctx) {
        logger.info(String.format("recommendation request from %s: %d", HOSTNAME, count));
        ctx.next();
    }

    private void timeout(RoutingContext ctx) {
        ctx.vertx().setTimer(3000, handler -> ctx.next());
    }

    private void getRecommendations(RoutingContext ctx) {
        if (misbehave) {
            count = 0;
            logger.info(String.format("Misbehaving %d", count));
            ctx.response().setStatusCode(503).end(String.format("recommendation misbehavior from '%s'\n", HOSTNAME));
        } else {
            count++;
            ctx.response().end(String.format(RESPONSE_STRING_FORMAT, HOSTNAME, count));
        }
    }

    private void misbehave(RoutingContext ctx) {
        this.misbehave = true;
        logger.info("'misbehave' has been set to 'true'");
        ctx.response().end("Following requests to '/' will return a 503\n");
    }

    private void behave(RoutingContext ctx) {
        this.misbehave = false;
        logger.info("'misbehave' has been set to 'false'");
        ctx.response().end("Following requests to '/' will return a 200\n");
    }

    public static void main(String[] args) {
        Vertx.vertx().deployVerticle(new RecommendationVerticle());
    }

}
