package com.imperva.shcf4j.client.config;


import com.imperva.shcf4j.HttpHost;
import lombok.Builder;
import lombok.Singular;
import lombok.Value;

import java.net.InetAddress;
import java.util.Collection;

/**
 * <b>RequestConfig</b>
 * <p/>
 * <p>
 * Holds configuration parameters for HTTP request.
 * </p>
 *
 * @author <font color="blue">Maxim Kirilov</font>
 *         Date: May 2014
 */
@Builder(toBuilder = true)
@Value
public final class RequestConfig {

    @Builder.Default
    private final boolean expectContinueEnabled = false;
    private final HttpHost proxy;
    private final InetAddress localAddress;
    @Builder.Default
    private final boolean staleConnectionCheckEnabled = true;
    private final String cookieSpec;
    @Builder.Default
    private final boolean redirectsEnabled = true;
    private final boolean relativeRedirectsAllowed;
    private final boolean circularRedirectsAllowed;
    private final int maxRedirects;
    private final boolean authenticationEnabled;
    @Singular("targetPreferredAuthScheme")
    private final Collection<String> targetPreferredAuthSchemes;
    @Singular("proxyPreferredAuthScheme")
    private final Collection<String> proxyPreferredAuthSchemes;
    @Builder.Default
    private final int connectionRequestTimeoutMilliseconds = -1;
    @Builder.Default
    private final int connectTimeoutMilliseconds = -1;
    @Builder.Default
    private final int socketTimeoutMilliseconds = -1;

}