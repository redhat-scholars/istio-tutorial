package com.redhat.developer.demos.customer.rest;

import org.eclipse.microprofile.rest.client.ext.ClientHeadersFactory;

import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public class BaggageHeadersFactory implements ClientHeadersFactory {
  

    @Override
    public MultivaluedMap<String, String> update(MultivaluedMap<String, String> incomingHeaders, MultivaluedMap<String, String> clientOutgoingHeaders) {
        MultivaluedHashMap<String, String> headers = new MultivaluedHashMap<>();
        String userAgent = incomingHeaders.getFirst("user-agent");
        headers.putSingle("baggage-user-agent", userAgent);

        String authorization = incomingHeaders.getFirst("Authorization");

        if (authorization != null) {
            headers.putSingle("Authorization", authorization);
        }

        String userPreference = incomingHeaders.getFirst("user-preference");
        if (userPreference != null) {
            headers.putSingle("user-preference", userPreference);
        }

        return headers;
    }

}
