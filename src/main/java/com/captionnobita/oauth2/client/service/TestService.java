/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/springframework/Service.java to edit this template
 */
package com.captionnobita.oauth2.client.service;

import com.captionnobita.oauth2.client.model.TestMsgRequest;
import com.captionnobita.oauth2.client.model.TestMsgResponse;
import java.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.oauth2.client.web.client.RequestAttributeClientRegistrationIdResolver;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

/**
 *
 * @author Nguyen Xuan Huy <captainnobita@gmail.com>
 */
@Service
public class TestService {
    private Logger log = LoggerFactory.getLogger(this.getClass().getName());
    private RestClient restClient;
    
    public TestService(RestClient restClient) {
        this.restClient = restClient;
    }
    
    @Scheduled(cron = "${app.scheduler}")
    @Async(value = "applicationTaskExecutor")
    public void autoGetDateUsingRestClient() {
        TestMsgRequest testMsgRequest = new TestMsgRequest();
        testMsgRequest.setClientDatetime(LocalDateTime.now());
        
        try {
            ResponseEntity<TestMsgResponse> responseEntityTestMsg = restClient.post()
                    .uri("https://localhost:8120/sample/test/currentDate")
                    .body(testMsgRequest)
                    .contentType(MediaType.APPLICATION_JSON)
                    .attributes(RequestAttributeClientRegistrationIdResolver.clientRegistrationId("oauth2-auth-server"))
                    .body(testMsgRequest)
                    .retrieve()
                    .toEntity(TestMsgResponse.class);

            log.info(responseEntityTestMsg.getBody().toString());
        } catch(Exception ex) {
            log.error("Loi:{}", ex.getMessage());
        }
        
    }
}
