/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.captionnobita.oauth2.client.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

/**
 *
 * @author Nguyen Xuan Huy <captainnobita@gmail.com>
 */
@Configuration
public class ExecutorConfig {
    @Bean(name = "myTaskScheduler")
    public TaskScheduler taskScheduler() {
        // Tạo một bean TaskScheduler cụ thể cho việc lập lịch
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(100); // Kích thước pool cho thread
        scheduler.setThreadNamePrefix("scheduled-task-");
        return scheduler;
    }

    @Bean(name = "myTaskExecutor")
    @Primary
    public TaskExecutor taskExecutor() {
        // Tạo một bean TaskExecutor cụ thể cho việc thực thi bất đồng bộ
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(100);
        executor.setMaxPoolSize(100);
        executor.setQueueCapacity(200);
        executor.setThreadNamePrefix("async-task-");
        executor.initialize();
        return executor;
    }
}
