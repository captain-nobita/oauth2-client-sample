/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.captionnobita.oauth2.client.filter;

import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class Oauth2RequestInterceptor implements ClientHttpRequestInterceptor {
    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        String requestBody = new String(body, StandardCharsets.UTF_8);
        
        Map<String, String> authentInfo = parseFormUrlEncoded(requestBody);
        String clientId = authentInfo.get("client_id");
        String clientSecret = authentInfo.get("client_secret");
        
        log.info("Begin GetToken URI:{} Method:{} Headers:{} client_id:{} client_secret:{}", 
                request.getURI(),
                request.getMethod(),
                request.getHeaders(),
                clientId,
                StringUtils.overlay(clientSecret, "*", 2, 6)
                );
        
        try {
            ClientHttpResponse response = execution.execute(request, body);

            log.info("Done GetToken URI:{} httpCode:{}",
                    request.getURI(),
                    response.getStatusCode()
            );
            return response;
        } catch(IOException ex) {
            log.info("Done GetToken URI:{} Exception:{}",
                    request.getURI(),
                    ex.getMessage()
            );
            throw ex;
        } 
    }
    
    private Map<String, String> parseFormUrlEncoded(String data) throws IOException {
        Map<String, String> formData = new HashMap<>();
        String[] pairs = data.split("&");
        for (String pair : pairs) {
            String[] keyValue = pair.split("=");
            if (keyValue.length == 2) {
                String key = URLDecoder.decode(keyValue[0], StandardCharsets.UTF_8.name());
                String value = URLDecoder.decode(keyValue[1], StandardCharsets.UTF_8.name());
                formData.put(key, value);
            }
        }
        return formData;
    }
}
