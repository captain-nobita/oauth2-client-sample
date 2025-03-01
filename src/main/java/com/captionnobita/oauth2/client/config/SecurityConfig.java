package com.captionnobita.oauth2.client.config;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
import com.captionnobita.oauth2.client.filter.Oauth2RequestInterceptor;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.security.oauth2.client.AuthorizedClientServiceOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.ClientCredentialsOAuth2AuthorizedClientProvider;
import org.springframework.security.oauth2.client.InMemoryOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientProvider;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.endpoint.RestClientClientCredentialsTokenResponseClient;
import org.springframework.security.oauth2.client.http.OAuth2ErrorResponseErrorHandler;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.http.converter.OAuth2AccessTokenResponseHttpMessageConverter;
import org.springframework.web.client.RestClient;

@Configuration
public class SecurityConfig {
    @Value("${app.oauth2.client.client-id}")
    private String clientId;
    
    @Value("${app.oauth2.client.client-secret}")
    private String clientSecret;
    
    @Value("${app.oauth2.client.client-authentication-method}")
    private String clientAuthenticationMethod;
    
    @Value("${app.oauth2.client.token-uri}")
    private String tokenUri;
    
    private RestClient restClient;
    
    @Autowired
    private HttpComponentsClientHttpRequestFactory httpComponentsClientHttpRequestFactory;
    
    @Autowired
    private Oauth2RequestInterceptor oauth2RequestInterceptor;

    @PostConstruct
    void initialize() {
        this.restClient = RestClient.builder()
                .messageConverters((messageConverters) -> {
                    messageConverters.clear();
                    messageConverters.add(new FormHttpMessageConverter());
                    messageConverters.add(new OAuth2AccessTokenResponseHttpMessageConverter());
                })
                .defaultStatusHandler(new OAuth2ErrorResponseErrorHandler())
                .requestFactory(httpComponentsClientHttpRequestFactory)
                .requestInterceptor(oauth2RequestInterceptor)
                // TODO: Customize the instance of RestClient as needed...
                .build();
    }
    
    @Bean
    public ClientRegistrationRepository clientRegistrationRepository() {
        return new InMemoryClientRegistrationRepository(this.clientRegistration());
    }

    private ClientRegistration clientRegistration() {
        return ClientRegistration.withRegistrationId("oauth2-auth-server")
                .clientId(clientId)
                .clientSecret(clientSecret)
                .clientAuthenticationMethod(new ClientAuthenticationMethod(clientAuthenticationMethod))
                .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
                .tokenUri(tokenUri)
                .clientName("oauth2-auth-client")
                .build();
    }
    
    @Bean
    public OAuth2AuthorizedClientService authorizedClientService(ClientRegistrationRepository clientRegistrationRepository) {
        InMemoryOAuth2AuthorizedClientService authorizedClientService = new InMemoryOAuth2AuthorizedClientService(clientRegistrationRepository);
        return authorizedClientService;
    }
    
    @Bean
    public RestClientClientCredentialsTokenResponseClient restClientClientCredentialsTokenResponseClient() {
        RestClientClientCredentialsTokenResponseClient restClientClientCredentialsTokenResponseClient =
                new RestClientClientCredentialsTokenResponseClient();
        
        restClientClientCredentialsTokenResponseClient.setRestClient(restClient);
        
        return restClientClientCredentialsTokenResponseClient;
    }
    
    @Bean
    public OAuth2AuthorizedClientProvider clientCredentialsOAuth2AuthorizedClientProvider(
            RestClientClientCredentialsTokenResponseClient restClientClientCredentialsTokenResponseClient) {
        ClientCredentialsOAuth2AuthorizedClientProvider clientProvider = new ClientCredentialsOAuth2AuthorizedClientProvider();
        clientProvider.setAccessTokenResponseClient(restClientClientCredentialsTokenResponseClient);
        return clientProvider;
    }
    
    
    @Bean
    public OAuth2AuthorizedClientManager authorizedClientManager(
            ClientRegistrationRepository clientRegistrationRepository,
            OAuth2AuthorizedClientService authorizedClientService,
            OAuth2AuthorizedClientProvider clientCredentialsOAuth2AuthorizedClientProvider
            ) {

        AuthorizedClientServiceOAuth2AuthorizedClientManager authorizedClientManager
                = new AuthorizedClientServiceOAuth2AuthorizedClientManager(
                        clientRegistrationRepository, authorizedClientService);
        authorizedClientManager.setAuthorizedClientProvider(clientCredentialsOAuth2AuthorizedClientProvider);

        return authorizedClientManager;
    }
}
