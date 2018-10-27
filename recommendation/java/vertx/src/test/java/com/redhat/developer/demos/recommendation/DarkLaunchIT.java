package com.redhat.developer.demos.recommendation;

import java.io.IOException;
import java.net.URL;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import org.arquillian.cube.istio.api.IstioResource;
import org.arquillian.cube.istio.api.RestoreIstioResource;
import org.arquillian.cube.istio.impl.IstioAssistant;
import org.arquillian.cube.openshift.impl.enricher.AwaitRoute;
import org.arquillian.cube.openshift.impl.enricher.RouteURL;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(Arquillian.class)
@IstioResource("classpath:dark-launch-redirect-traffic-to-new-version.yml")
@RestoreIstioResource("classpath:virtual-service-recommendation-v1.yml")
public class DarkLaunchIT {

    @RouteURL("customer")
    @AwaitRoute
    private URL url;

    @ArquillianResource
    IstioAssistant istioAssistant;

    /**
     * Istio resources takes some time until all Envoy proxies of the cluster receives the update.
     * In case of local installations might take one second in case of real clusters some tenths of seconds.
     */
    @Before
    public void waitUntilIstioResourcesArePopulated() {
        istioAssistant.await(createRequestForRecommendationV2(), response -> {
            try {
                return response.body().string().contains("v2");
            } catch (IOException e) {
                return false;
            }
        });
    }

    @Test
    public void should_return_accessing_v2_message() throws IOException {

        // Given
        final Request request = createRequestForRecommendationV2();

        // When
        final String content = makeRequest(request);

        // Then
        assertThat(content)
            .startsWith("customer => preference => recommendation v2 from");

    }

    private String makeRequest(Request request) throws IOException {
        final HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.HEADERS);

        OkHttpClient client = new OkHttpClient.Builder()
            .addNetworkInterceptor(interceptor)
            .build();
        try(Response response = client.newCall(request).execute()) {
            return response.body().string();
        }
    }

    private Request createRequestForRecommendationV2() {
        return new Request.Builder()
                .url(url.toString())
                .addHeader("User-Agent", "Recommendation-v2-DarkLaunch-Test")
                .build();
    }

}
