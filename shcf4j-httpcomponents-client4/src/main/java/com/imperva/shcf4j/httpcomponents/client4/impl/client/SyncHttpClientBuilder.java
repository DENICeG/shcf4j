package com.imperva.shcf4j.httpcomponents.client4.impl.client;

import com.imperva.shcf4j.HttpHost;
import com.imperva.shcf4j.HttpMessage;
import com.imperva.shcf4j.client.CredentialsProvider;
import com.imperva.shcf4j.client.SyncHttpClient;
import com.imperva.shcf4j.client.config.RequestConfig;
import com.imperva.shcf4j.config.SocketConfig;
import com.imperva.shcf4j.conn.routing.HttpRoutePlanner;
import com.imperva.shcf4j.conn.ssl.SSLSessionStrategy;
import com.imperva.shcf4j.httpcomponents.client4.impl.ConversionUtils;
import com.imperva.shcf4j.httpcomponents.client4.impl.HttpMessageWrapper;
import org.apache.http.conn.socket.LayeredConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.conn.DefaultRoutePlanner;
import org.apache.http.impl.conn.DefaultSchemePortResolver;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * <b>SyncHttpClientBuilder</b>
 * <p>
 * Builder for {@link SyncHttpClient} instances.
 * </p>
 * <p>
 * When a particular component is not explicitly this class will
 * use its default implementation.
 * <ul>
 * <li>ssl.TrustManagerFactory.algorithm</li>
 * <li>javax.net.ssl.trustStoreType</li>
 * <li>javax.net.ssl.trustStore</li>
 * <li>javax.net.ssl.trustStoreProvider</li>
 * <li>javax.net.ssl.trustStorePassword</li>
 * <li>ssl.KeyManagerFactory.algorithm</li>
 * <li>javax.net.ssl.keyStoreType</li>
 * <li>javax.net.ssl.keyStore</li>
 * <li>javax.net.ssl.keyStoreProvider</li>
 * <li>javax.net.ssl.keyStorePassword</li>
 * <li>https.protocols</li>
 * <li>https.cipherSuites</li>
 * <li>http.proxyHost</li>
 * <li>http.proxyPort</li>
 * <li>http.nonProxyHosts</li>
 * <li>http.keepAlive</li>
 * <li>http.maxConnections</li>
 * <li>http.agent</li>
 * </ul>
 * Please note that some settings used by this class can be mutually
 * exclusive and may not apply when building {@link SyncHttpClient}
 * instances.
 *
 * @author maxim.kirilov
 */
class SyncHttpClientBuilder implements com.imperva.shcf4j.SyncHttpClientBuilder {

    private final org.apache.http.impl.client.HttpClientBuilder builder;


    protected SyncHttpClientBuilder(org.apache.http.impl.client.HttpClientBuilder httpClientBuilder) {
        this.builder = httpClientBuilder;
    }

    protected SyncHttpClientBuilder() {
        this(org.apache.http.impl.client.HttpClients.custom());
    }


    /**
     * @return a {@code SyncHttpClient} that the builder produce
     */
    @Override
    public SyncHttpClient build() {
        return new SimpleSyncHttpClient(builder.build());
    }


    /**
     * @param strategy the SSL socket creation strategy
     * @return {@code SyncHttpClientBuilder}
     */
    @Override
    public com.imperva.shcf4j.httpcomponents.client4.impl.client.SyncHttpClientBuilder setSSLSessionStrategy(final SSLSessionStrategy strategy)  throws SSLException {
        Objects.requireNonNull(strategy, "strategy");

        try {
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(
                    strategy.getKeyManagerFactory() != null ?
                            strategy.getKeyManagerFactory().getKeyManagers() : null,
                    strategy.getTrustManagerFactory() != null ?
                            strategy.getTrustManagerFactory().getTrustManagers() : null,
                    null
            );
            LayeredConnectionSocketFactory socketFactory = new SSLConnectionSocketFactory(
                    sslContext,
                    strategy.getSupportedProtocols(),
                    strategy.getSupportedCipherSuites(),
                    strategy.getHostnameVerifier());

            this.builder.setSSLSocketFactory(socketFactory);
            return this;
        } catch (NoSuchAlgorithmException | KeyManagementException e){
            throw new SSLException(e);
        }
    }


    /**
     * Disables connection state tracking.
     *
     * @return {@code SyncHttpClientBuilder}
     */
    public com.imperva.shcf4j.httpcomponents.client4.impl.client.SyncHttpClientBuilder disableConnectionState() {
        builder.disableConnectionState();
        return this;
    }

    /**
     * Disables automatic content decompression.
     *
     * @return {@code SyncHttpClientBuilder}
     */
    public com.imperva.shcf4j.httpcomponents.client4.impl.client.SyncHttpClientBuilder disableContentCompression() {
        builder.disableContentCompression();
        return this;
    }


    /**
     * Assigns maximum total connection value.
     *
     * @param maxConnTotal allowed in this client instance
     * @return {@code SyncHttpClientBuilder}
     */
    public com.imperva.shcf4j.httpcomponents.client4.impl.client.SyncHttpClientBuilder setMaxConnTotal(int maxConnTotal) {
        builder.setMaxConnTotal(maxConnTotal);
        return this;
    }

    /**
     * Assigns default {@link SocketConfig}.
     *
     * @param socketConfig object
     * @return {@code SyncHttpClientBuilder}
     */
    @Override
    public com.imperva.shcf4j.httpcomponents.client4.impl.client.SyncHttpClientBuilder setDefaultSocketConfig(SocketConfig socketConfig) {
        Objects.requireNonNull(socketConfig, "socketConfig");
        org.apache.http.config.SocketConfig.Builder SocketBuilder = org.apache.http.config.SocketConfig.custom();
        SocketBuilder
                .setSoKeepAlive(socketConfig.isSoKeepAlive())
                .setSoLinger(socketConfig.getSoLingerSeconds())
                .setSoReuseAddress(socketConfig.isSoReuseAddress())
                .setSoTimeout(socketConfig.getSoTimeoutMilliseconds())
                .setTcpNoDelay(socketConfig.isTcpNoDelay());

        builder.setDefaultSocketConfig(SocketBuilder.build());
        return this;
    }

    /**
     * Assigns retryCount.
     *
     * @param retryCount of single request
     * @return {@code SyncHttpClientBuilder}
     */
    public com.imperva.shcf4j.httpcomponents.client4.impl.client.SyncHttpClientBuilder setRetryCount(Integer retryCount) {

        DefaultHttpRequestRetryHandler retryhandler = new DefaultHttpRequestRetryHandler(retryCount, true);
        builder.setRetryHandler(retryhandler);
        return this;
    }

    /**
     * Assigns default {@link RequestConfig} instance which will be used
     * for request execution if not explicitly set in the client4 execution
     * context.
     *
     * @param config default config for every outgoing request
     * @return {@code SyncHttpClientBuilder}
     */
    @Override
    public final com.imperva.shcf4j.httpcomponents.client4.impl.client.SyncHttpClientBuilder setDefaultRequestConfig(final RequestConfig config) {
        Objects.requireNonNull(config, "config");
        builder.setDefaultRequestConfig(ConversionUtils.convert(config));
        return this;
    }

    /**
     * Assigns default proxy value.
     *
     * @param proxy object for every outgoing request
     * @return {@code SyncHttpClientBuilder}
     */
    @Override
    public com.imperva.shcf4j.httpcomponents.client4.impl.client.SyncHttpClientBuilder setProxy(HttpHost proxy) {
        Objects.requireNonNull(proxy, "proxy");
        builder.setProxy(ConversionUtils.convert(proxy));
        return this;
    }


    /**
     * @param cp credentials provider for various authentication schemes
     * @return {@code SyncHttpClientBuilder}
     */
    @Override
    public com.imperva.shcf4j.httpcomponents.client4.impl.client.SyncHttpClientBuilder setDefaultCredentialsProvider(CredentialsProvider cp) {
        Objects.requireNonNull(cp, "cp");
        builder.setDefaultCredentialsProvider(ConversionUtils.convert(cp));
        return this;
    }


    /**
     * @param routePlanner for outgoing http requests
     * @return {@code SyncHttpClientBuilder}
     */
    public com.imperva.shcf4j.httpcomponents.client4.impl.client.SyncHttpClientBuilder setRoutePlanner(final HttpRoutePlanner routePlanner) {
        Objects.requireNonNull(routePlanner, "routePlanner");
        final org.apache.http.conn.routing.HttpRoutePlanner defaultRoutePlanner =
                new DefaultRoutePlanner(DefaultSchemePortResolver.INSTANCE);
        builder.setRoutePlanner((target, request, context) -> {

            HttpHost t1 =
                    HttpHost
                            .builder()
                            .hostname(target.getHostName())
                            .schemeName(target.getSchemeName())
                            .port(target.getPort())
                            .build();

            t1 = routePlanner.determineRoute(t1, new HttpMessageWrapper(request));

            return defaultRoutePlanner.determineRoute(ConversionUtils.convert(t1), request, context);
        });
        return this;
    }

    /**
     * Adds a list of interceptors to the client4, the order is according to the passed list iterator
     * implementation.
     *
     * @param interceptors a list of interceptors that executed upon every request
     * @return {@code SyncHttpClientBuilder}
     */
    public com.imperva.shcf4j.httpcomponents.client4.impl.client.SyncHttpClientBuilder setRequestInterceptors(List<Consumer<HttpMessage>> interceptors) {
        for (Consumer<HttpMessage> interceptor : interceptors) {
            builder.addInterceptorLast(new HttpRequestInterceptorAdapter(interceptor));
        }
        return this;
    }


}
