package com.imperva.shcf4j.config;

import com.imperva.shcf4j.Header;
import com.imperva.shcf4j.HttpRequest;
import com.imperva.shcf4j.client.config.RequestConfig;
import com.imperva.shcf4j.client.protocol.ClientContext;
import com.imperva.shcf4j.HttpClientBaseTest;
import org.junit.Test;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.concurrent.TimeUnit;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;

/**
 * <b>RequestConfigTest</b>
 */
public abstract class RequestConfigTest extends HttpClientBaseTest {


    @Test(expected = Exception.class)
    public void requestTimeoutTest() throws IOException {

        long millisecondsDelay = TimeUnit.MILLISECONDS.convert(1, TimeUnit.SECONDS);
        String uri = "/my/resource";
        instanceRule.stubFor(get(urlEqualTo(uri))
                .withHeader(HEADER_ACCEPT, equalTo("text/xml"))
                .willReturn(
                        aResponse()
                                .withStatus(HttpURLConnection.HTTP_OK)
                                .withFixedDelay((int) millisecondsDelay)
                                .withHeader(HEADER_CONTENT_TYPE, "text/xml")
                )
        );

        HttpRequest request =
                HttpRequest
                        .builder()
                        .getRequest()
                        .uri(uri)
                        .header(Header.builder().name(HttpClientBaseTest.HEADER_ACCEPT).value("text/xml").build())
                        .build();

        execute(HOST, request,
                ClientContext
                        .builder()
                        .requestConfig(
                                RequestConfig
                                        .builder()
                                        .socketTimeoutMilliseconds( (int)millisecondsDelay / 2)
                                        .build())
                        .build());
    }

}


