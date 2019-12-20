package com.redhat.developer.demos.preference.rest;

import io.quarkus.runtime.annotations.RegisterForReflection;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import org.eclipse.microprofile.rest.client.ext.ClientHeadersFactory;

@RegisterForReflection
public class BaggageHeadersFactory implements ClientHeadersFactory {

    @Override
    public MultivaluedMap<String, String> update(MultivaluedMap<String, String> incomingHeaders, MultivaluedMap<String, String> clientOutgoingHeaders) {
        MultivaluedHashMap<String, String> headers = new MultivaluedHashMap<>();
        String userAgent = incomingHeaders.getFirst("baggage-user-agent");
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
