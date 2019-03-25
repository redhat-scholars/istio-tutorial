package com.redhat.developer.demos.customer.rest;

import org.eclipse.microprofile.rest.client.annotation.RegisterClientHeaders;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

@RegisterClientHeaders
@RegisterRestClient
public interface RecommendationService {

    @Path("/")
    @GET
    @Produces("text/plain")
    public String getPreference();

}
