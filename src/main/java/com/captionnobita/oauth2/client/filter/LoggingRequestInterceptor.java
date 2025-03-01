/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.captionnobita.oauth2.client.filter;

import java.io.ByteArrayInputStream;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class LoggingRequestInterceptor implements ClientHttpRequestInterceptor {
    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        logRequestDetails(request, body);
        
        String requestBody = new String(body, StandardCharsets.UTF_8);
        LocalDateTime deliveryDatetime = LocalDateTime.now();
        
        try {
            ClientHttpResponse response = execution.execute(request, body);
            byte[] responseBody = logResponseDetails(request, requestBody, deliveryDatetime, response);
            return new CustomClientHttpResponse(response, responseBody);
        } catch(IOException ex) {
            logResponseDetails(request, requestBody, deliveryDatetime, null);
            throw ex;
        }
    }

    private void logRequestDetails(HttpRequest request, byte[] body) throws IOException {
        log.info("URI:{} Method:{} Headers:{} Body:{}", 
                request.getURI(),
                request.getMethod(),
                request.getHeaders(),
                new String(body, StandardCharsets.UTF_8)
                );
    }

    private byte[] logResponseDetails(HttpRequest request, String requestBody, LocalDateTime deliveryDatetime, ClientHttpResponse response) throws IOException {
        
        byte[] responseBody = new byte[0];
        String responseBodyStr = null;
        Integer httpRespCode = null;
        LocalDateTime respDateTime = null;
        LocalDateTime modifDateTime = LocalDateTime.now();
        
        if(response != null) {
            if (response.getBody() != null) {
                responseBody = IOUtils.toByteArray(response.getBody());
                if (responseBody.length > 0) {
                    responseBodyStr = new String(responseBody, StandardCharsets.UTF_8);
                }
            }
            httpRespCode = response.getStatusCode().value();
            respDateTime = LocalDateTime.now();
            log.info("Headers:{} StatusCode:{} Response:{}", response.getHeaders(), response.getStatusCode(), responseBodyStr);
        }  
        
        return responseBody;
    }
    
    private static class CustomClientHttpResponse implements ClientHttpResponse {

        private final ClientHttpResponse originalResponse;
        private final byte[] body;

        public CustomClientHttpResponse(ClientHttpResponse originalResponse, byte[] body) {
            this.originalResponse = originalResponse;
            this.body = body;
        }

        @Override
        public String getStatusText() throws IOException {
            return originalResponse.getStatusText();
        }

        @Override
        public InputStream getBody() throws IOException {
            return new ByteArrayInputStream(body);
        }

        @Override
        public HttpHeaders getHeaders() {
            return originalResponse.getHeaders();
        }

        @Override
        public HttpStatusCode getStatusCode() throws IOException {
            return originalResponse.getStatusCode();
        }

        @Override
        public void close() {
            originalResponse.close();
        }
    }
}
