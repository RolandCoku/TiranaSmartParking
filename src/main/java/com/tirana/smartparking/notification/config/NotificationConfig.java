package com.tirana.smartparking.notification.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableScheduling
public class NotificationConfig {

    @Bean
    public ObjectMapper notificationObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        // Configure ObjectMapper for notification-specific serialization if needed
        return mapper;
    }
}
