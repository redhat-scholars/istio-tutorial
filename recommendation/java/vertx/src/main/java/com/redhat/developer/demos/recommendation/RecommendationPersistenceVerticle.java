package com.redhat.developer.demos.recommendation;

import java.util.ArrayList;
import java.util.List;

import org.flywaydb.core.Flyway;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.healthchecks.HealthCheckHandler;
import io.vertx.ext.healthchecks.Status;
import io.vertx.ext.jdbc.JDBCClient;
import io.vertx.ext.sql.ResultSet;
import io.vertx.ext.sql.SQLConnection;
import io.vertx.ext.sql.UpdateResult;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;

public class RecommendationPersistenceVerticle extends AbstractVerticle {

    private static final String PASSWORD = "admin123";
    private static final String USER = "admin";
    private static final String URL = "jdbc:postgresql://postgres:5432/recommendation";

    private static final String RESPONSE_RECOMMENDATION_STRING_FORMAT = "recommendation v4 [%s] from '%s': %d\n";

    private static final String HOSTNAME = parseContainerIdFromHostname(
        System.getenv().getOrDefault("HOSTNAME", "unknown")
    );

    private static final int LISTEN_ON = Integer.parseInt(
        System.getenv().getOrDefault("LISTEN_ON", "8080")
    );

    static String parseContainerIdFromHostname(String hostname) {
        return hostname.replaceAll("recommendation-v\\d+-", "");
    }

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private JDBCClient jdbcClient;

    private void initJdbc(Vertx vertx) {
        populateData();
        jdbcClient = JDBCClient.createShared(vertx, new JsonObject()
            .put("url", URL)
            .put("driver_class", "org.postgresql.Driver")
            .put("user", USER)
            .put("password", PASSWORD)
            .put("max_pool_size", 30));
    }

    private void populateData() {
        final Flyway flyway = Flyway
                                .configure()
                                .dataSource(URL, USER, PASSWORD)
                                .load();
        flyway.migrate();
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
    public void start() throws Exception {

        
        Router router = Router.router(vertx);
        router.get("/").handler(this::logging);
        
        router.route().handler(BodyHandler.create());
        initJdbc(vertx);
        router.get("/").handler(this::getRecommendationsFromDb);
        router.post("/").handler(this::addRecommendation);
        // router.post("/").handler(this::addRecommendationToBothColumns);
        // router.post("/").handler(this::addRecommendationToNewColumn);

        HealthCheckHandler hc = HealthCheckHandler.create(vertx);
        hc.register("dummy-health-check", future -> future.complete(Status.OK()));
        router.get("/health/ready").handler(hc);
        router.get("/health/live").handler(hc);

        vertx.createHttpServer().requestHandler(router::accept).listen(LISTEN_ON);
    }

    private void getRecommendationsFromDb(RoutingContext ctx) {
        jdbcClient.getConnection(res -> {
            if (res.succeeded()) {
                try (final SQLConnection connection = res.result()) {
                    String sql = "SELECT * FROM recommendation;";
                    connection.query(sql, rs -> {
                        final ResultSet resultSet = rs.result();

                        if (resultSet != null) {
                            count++;
                            final List<String> recommendations = findRecommendation(resultSet);
                            // final List<String> recommendations = findRecommendationNew(resultSet);
                            ctx.response().end(String.format(RESPONSE_RECOMMENDATION_STRING_FORMAT, String.join(",", recommendations), HOSTNAME, count));
                        } else {
                            ctx.response()
                                .setStatusCode(500)
                                .end(String.format("Connection to database couldn't be established: %s", res.cause()));
                        }
                    });
                }
            } else {
                ctx.response()
                    .setStatusCode(500)
                    .end(String.format("Connection to database couldn't be established: %s", res.cause()));
            }
        });
    }

    private List<String> findRecommendationNew(final ResultSet resultSet) {
        final List<String> recommendations = new ArrayList<>();
        for (JsonObject row : resultSet.getRows()) {
            recommendations.add(row.getString("movie_name"));            
        }

        return recommendations;
    }

    private List<String> findRecommendation(final ResultSet resultSet) {
        final List<String> recommendations = new ArrayList<>();
        for (JsonObject row : resultSet.getRows()) {
            recommendations.add(row.getString("name"));            
        }

        return recommendations;
    } 

    private void addRecommendation(RoutingContext ctx) {
        final String recommendation = ctx.getBodyAsString();
        final JsonArray attributes = new JsonArray().add(recommendation);
        insert(ctx, "INSERT INTO recommendation(name) VALUES (?);", attributes);
    }

    private void addRecommendationToBothColumns(RoutingContext ctx) {
        final String recommendation = ctx.getBodyAsString();
        final JsonArray attributes = new JsonArray()
                                            .add(recommendation)
                                            .add(recommendation);
        insert(ctx, "INSERT INTO recommendation(name, movie_name) VALUES (?,?);", attributes);
    }

    private void addRecommendationToNewColumn(RoutingContext ctx) {
        final String recommendation = ctx.getBodyAsString();
        final JsonArray attributes = new JsonArray().add(recommendation);
        insert(ctx, "INSERT INTO recommendation(movie_name) VALUES (?);", attributes);
    }

    private void insert(RoutingContext ctx, String sql, JsonArray attributes) {
        jdbcClient.getConnection(res -> {
            if (res.succeeded()) {
                try (final SQLConnection connection = res.result()) {
                    connection.updateWithParams(sql, attributes, insert -> {
                        final UpdateResult ur = insert.result();
                        if (ur != null) {
                            ctx.response()
                                .setStatusCode(201)
                                .end(Integer.toString(ur.getKeys().getInteger(0)));
                        } else {
                            ctx.response()
                                .setStatusCode(500)
                                .end(String.format("Connection to database couldn't be established: %s", res.cause()));
                        }
                    });
                }
            } else {
                ctx.response()
                    .setStatusCode(500)
                    .end(String.format("Connection to database couldn't be established: %s", res.cause()));
            }
        });
    }

    private void logging(RoutingContext ctx) {
        logger.info(String.format("recommendation request from %s: %d", HOSTNAME, count));
        ctx.next();
    }

    public static void main(String[] args) {
        Vertx.vertx().deployVerticle(new RecommendationPersistenceVerticle());
    }

}
