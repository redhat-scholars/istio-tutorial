package com.redhat.developer.demos.customer.rest;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Response;


@ApplicationScoped
@Path("/")
public class CustomerEndpoint {

    private static final String RESPONSE_STRING_FORMAT = "customer => %s\n";

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Inject
    @ConfigProperty(name = "preferences.api.url", defaultValue = "http://preference:8080")
    private String remoteURL;


    @GET
    @Produces("text/plain")
    public Response getCustomer(@HeaderParam("user-agent") String userAgent) {
        try {
            Client client = ClientBuilder.newClient();
            Response res = client.target(remoteURL).request("text/plain").header("user-agent", userAgent).get();
            if (res.getStatus() == Response.Status.OK.getStatusCode()){
                return Response.ok(String.format(RESPONSE_STRING_FORMAT, res.readEntity(String.class))).build();
            }else{
                logger.warn("Non HTTP 20x trying to get the response from preference service: " + res.getStatus());
                return Response
                        .status(Response.Status.SERVICE_UNAVAILABLE)
                        .entity(String.format(RESPONSE_STRING_FORMAT,
                                String.format("Error: %d - %s", res.getStatus(), res.readEntity(String.class)))
                        )
                        .build();
            }
        } catch (ProcessingException ex) {
            logger.warn("Exception trying to get the response from preference service.", ex);
            return Response
                    .status(Response.Status.SERVICE_UNAVAILABLE)
                    .entity(String.format(RESPONSE_STRING_FORMAT, ex.getCause().getClass().getSimpleName() + ": " + ex.getCause().getMessage()))
                    .build();
        }
    }
}
