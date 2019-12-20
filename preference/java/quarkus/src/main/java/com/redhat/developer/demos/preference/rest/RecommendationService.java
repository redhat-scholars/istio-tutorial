package com.redhat.developer.demos.preference.rest;

import org.eclipse.microprofile.rest.client.annotation.RegisterClientHeaders;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@RegisterClientHeaders(BaggageHeadersFactory.class)
@RegisterRestClient
public interface RecommendationService {

    @Path("/")
    @GET
    @Produces("text/plain")
    public String getRecommendation();

}
