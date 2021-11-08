package org.acme;

import java.io.InputStream;
import java.net.UnknownHostException;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.eclipse.microprofile.rest.client.inject.RestClient;

@Path("/bubble")
public class BubbleResource {
   
    @RestClient
    BubbleService bubbleService;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public InputStream bubble() throws UnknownHostException {
        return bubbleService.bubble();
    }

}