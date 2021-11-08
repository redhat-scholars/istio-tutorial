package org.acme;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.atomic.AtomicInteger;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.eclipse.microprofile.config.inject.ConfigProperty;

@Path("/bubble")
public class BubbleResource {

    @ConfigProperty(name = "bubble.color")
    String color;

    private boolean misbehave;

    private AtomicInteger counter = new AtomicInteger(0);

    @GET
    @Path("/misbehave")
    @Produces(MediaType.TEXT_PLAIN)
    public String misbehave() {
        misbehave = true;
        return "Following calls will return a 5XX error code";
    }

    @GET
    @Path("/behave")
    @Produces(MediaType.TEXT_PLAIN)
    public String behave() {
        misbehave = false;
        return "Back to normal";
    }

    @GET
    public Bubble bubble() throws UnknownHostException {
        return new Bubble(  color, 
                            InetAddress.getLocalHost().getHostName(), 
                            counter.incrementAndGet()
                        );
    }

    /**@GET
    @Path("/stream")
    @Produces(MediaType.SERVER_SENT_EVENTS)
    @SseElementType("application/json")
    public Multi<Bubble> stream() {
        Multi.createFrom()
                .ticks().every(Duration.ofMillis(200))
                .
    }**/
}