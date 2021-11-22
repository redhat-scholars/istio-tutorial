package org.acme;

import java.net.UnknownHostException;
import java.util.Map;

import javax.inject.Inject;
import javax.json.JsonObject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;

import org.eclipse.microprofile.rest.client.inject.RestClient;

@Path("/bubble")
public class BubbleResource {
   
    @RestClient
    BubbleService bubbleService;

    @Inject
    Statistics statistics;

    @GET
    public JsonObject bubble() throws UnknownHostException {
        final JsonObject bubble = bubbleService.bubble();
        statistics.add(bubble);
        return bubble;
    }

    @GET
    @Path("/statistics")
    public Statistics statistics() {
        return statistics;
    }

}