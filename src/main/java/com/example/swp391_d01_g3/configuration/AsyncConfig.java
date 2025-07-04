package com.example.swp391_d01_g3.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync
public class AsyncConfig {

    /**
     * Cấu hình thread pool cho việc gửi email bất đồng bộ
     */
    @Bean(name = "emailTaskExecutor")
    public Executor emailTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        
        // Số lượng thread cơ bản (luôn chạy)
        executor.setCorePoolSize(2);
        
        // Số lượng thread tối đa
        executor.setMaxPoolSize(5);
        
        // Kích thước queue chờ
        executor.setQueueCapacity(100);
        
        // Prefix cho tên thread
        executor.setThreadNamePrefix("Email-");
        
        // Chờ các task hoàn thành khi shutdown
        executor.setWaitForTasksToCompleteOnShutdown(true);
        
        // Thời gian chờ tối đa khi shutdown (30 giây)
        executor.setAwaitTerminationSeconds(30);
        
        // Khởi tạo executor
        executor.initialize();
        
        return executor;
    }
} 