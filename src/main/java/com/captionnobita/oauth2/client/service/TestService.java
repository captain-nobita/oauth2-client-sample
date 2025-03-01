/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/springframework/Service.java to edit this template
 */
package com.captionnobita.oauth2.client.service;

import java.time.LocalDateTime;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/**
 *
 * @author Nguyen Xuan Huy <captainnobita@gmail.com>
 */
@Service
@Slf4j
public class TestService {
    @Autowired
    private RetriableClientService retriableClientService;
    
    @Scheduled(fixedRate = 15000)
    @Async(value = "applicationTaskExecutor")
    public void autoGetDateUsingRestClient() {
        retriableClientService.getDateUsingRestClient(LocalDateTime.now());
        
    }
}
