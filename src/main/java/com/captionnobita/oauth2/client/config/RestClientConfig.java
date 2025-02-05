/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.captionnobita.oauth2.client.config;

import com.captionnobita.oauth2.client.utils.LoggingRequestInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.web.client.OAuth2ClientHttpRequestInterceptor;
import org.springframework.web.client.RestClient;

/**
 *
 * @author Nguyen Xuan Huy <captainnobita@gmail.com>
 */
@Configuration
public class RestClientConfig {
    @Autowired
    private HttpComponentsClientHttpRequestFactory httpComponentsClientHttpRequestFactory;
    
    @Bean
    public RestClient restClient(RestClient.Builder builder, OAuth2AuthorizedClientManager authorizedClientManager) {

        
        OAuth2ClientHttpRequestInterceptor requestInterceptor
                = new OAuth2ClientHttpRequestInterceptor(authorizedClientManager);

        return builder
                .requestInterceptor(requestInterceptor)
                .requestInterceptor(new LoggingRequestInterceptor())
                .requestFactory(httpComponentsClientHttpRequestFactory)
                .build();
    }
    
    

}
