/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.captionnobita.oauth2.client.config;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;
import javax.net.ssl.SSLContext;
import org.apache.hc.client5.http.ConnectionKeepAliveStrategy;
import org.apache.hc.client5.http.config.ConnectionConfig;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.client5.http.io.HttpClientConnectionManager;
import org.apache.hc.client5.http.ssl.DefaultClientTlsStrategy;
import org.apache.hc.client5.http.ssl.NoopHostnameVerifier;
import org.apache.hc.core5.http.HeaderElement;
import org.apache.hc.core5.http.HeaderElements;
import org.apache.hc.core5.http.HttpResponse;
import org.apache.hc.core5.http.message.MessageSupport;
import org.apache.hc.core5.http.protocol.HttpContext;
import org.apache.hc.core5.ssl.SSLContexts;
import org.apache.hc.core5.ssl.TrustStrategy;
import org.apache.hc.core5.util.Args;
import org.apache.hc.core5.util.TimeValue;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;

/**
 *
 * @author Nguyen Xuan Huy <captainnobita@gmail.com>
 */
@Configuration
public class HttpClientConfig {
    @Value("${http.max-total-connections:100}")
    private int maxTotalConnections;

    @Value("${http.max-connections-per-route:20}")
    private int maxConnectionsPerRoute;

    @Value("${http.connection-timeout:15}")
    private int connectionTimeout;

    @Value("${http.socket-timeout:15}")
    private int socketTimeout;

    @Value("${http.keep-alive-duration:5}")
    private int forceKeepAliveDuration;
    
    @Bean
    public HttpComponentsClientHttpRequestFactory httpComponentsClientHttpRequestFactory() {
        //Đoạn này để by pass SSL
        TrustStrategy acceptingTrustStrategy = (X509Certificate[] chain, String authType) -> true;

        SSLContext sslContext = null;
        try {
            sslContext = SSLContexts.custom()
                    .loadTrustMaterial(null, acceptingTrustStrategy)
                    .build();
        } catch (KeyManagementException | KeyStoreException | NoSuchAlgorithmException ex) {
            System.exit(1);
        }

        DefaultClientTlsStrategy dcts = new DefaultClientTlsStrategy(sslContext, new NoopHostnameVerifier());

        ConnectionConfig connConfig = ConnectionConfig.custom()
                .setSocketTimeout(socketTimeout, TimeUnit.SECONDS)
                .setConnectTimeout(connectionTimeout, TimeUnit.SECONDS)
                .build();

        HttpClientConnectionManager connectionManager = PoolingHttpClientConnectionManagerBuilder
                .create()
                .setMaxConnTotal(maxTotalConnections)
                .setMaxConnPerRoute(maxConnectionsPerRoute)
                .setTlsSocketStrategy(dcts)
                .setDefaultConnectionConfig(connConfig)
                .build();

        final ConnectionKeepAliveStrategy myStrategy = new ConnectionKeepAliveStrategy() {
            @Override
            public TimeValue getKeepAliveDuration(HttpResponse response, HttpContext context) {
                Args.notNull(response, "HTTP response");
                final Iterator<HeaderElement> it = MessageSupport.iterate(response, HeaderElements.KEEP_ALIVE);

                if (it.hasNext()) {
                    final HeaderElement he = it.next();
                    final String param = he.getName();
                    final String value = he.getValue();
                    if (value != null && param.equalsIgnoreCase("timeout")) {
                        try {
                            return TimeValue.ofSeconds(Long.parseLong(value));
                        } catch (final NumberFormatException ignore) {
                        }
                    }
                }
                return TimeValue.ofSeconds(forceKeepAliveDuration);
            }
        };

        CloseableHttpClient httpClient = HttpClients.custom()
                .setConnectionManager(connectionManager)
                .setDefaultRequestConfig(RequestConfig.DEFAULT)
                .setKeepAliveStrategy(myStrategy)
                .build();

        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
        factory.setHttpClient(httpClient);
        return factory;
    }
}
