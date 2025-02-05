/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.captionnobita.oauth2.client.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class LoggingRequestInterceptor implements ClientHttpRequestInterceptor {

    private Logger logger = LoggerFactory.getLogger("raw");

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        logRequestDetails(request, body);
        ClientHttpResponse response = execution.execute(request, body);
        logResponseDetails(response);
        return response;
    }

    private void logRequestDetails(HttpRequest request, byte[] body) throws IOException {
        logger.info("URI:{} Method:{} Headers:{} Body:{}", 
                request.getURI(),
                request.getMethod(),
                request.getHeaders(),
                new String(body, StandardCharsets.UTF_8)
                );
    }

    private void logResponseDetails(ClientHttpResponse response) throws IOException {
        logger.info("Headers:{} StatusCode:{}", response.getHeaders(), response.getStatusCode());
    }
}
