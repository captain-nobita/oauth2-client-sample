/*
 * Copyright 2025 Nguyen Xuan Huy <huynx@napas.com.vn>.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.captionnobita.oauth2.client.service;

import com.captionnobita.oauth2.client.model.TestMsgRequest;
import com.captionnobita.oauth2.client.model.TestMsgResponse;
import java.time.LocalDateTime;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.security.oauth2.client.web.client.RequestAttributeClientRegistrationIdResolver;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

/**
 *
 * @author Nguyen Xuan Huy <huynx@napas.com.vn>
 */
@Service
@Slf4j
public class RetriableClientService {
    @Autowired
    private RestClient restClient;

    @Retryable(
            retryFor = {ResourceAccessException.class}, // Bắt lỗi timeout
            maxAttempts = 3, // Retry tối đa 3 lần
            backoff = @Backoff(delay = 10000) // Chờ 2s trước khi retry
    )

    public void getDateUsingRestClient(LocalDateTime clientDate) {
        TestMsgRequest testMsgRequest = new TestMsgRequest();
        testMsgRequest.setClientDatetime(clientDate);

        try {
            ResponseEntity<TestMsgResponse> responseEntityTestMsg = restClient.post()
                    .uri("https://localhost:8120/sample/test/currentDate")
                    .body(testMsgRequest)
                    .contentType(MediaType.APPLICATION_JSON)
                    .attributes(RequestAttributeClientRegistrationIdResolver.clientRegistrationId("oauth2-auth-server"))
                    .body(testMsgRequest)
                    .retrieve()
                    .toEntity(TestMsgResponse.class);

            log.info(responseEntityTestMsg.getBody().getServerDatetime().toString());
        } catch(RestClientResponseException ex) {
            log.info("Loi:{}", ex.getStatusText());
        }
        
    }

    @Recover
    public void fallback(ResourceAccessException e, LocalDateTime clientDate) {
        log.info ("Fallback response due to timeout. InputTime:{} error:{}", clientDate, e.getMessage());
    }
}
