package com.tirana.smartparking.maintenance.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "schedulers")
@Data
public class SchedulerConfig {
    
    private ActivateBookings activateBookings = new ActivateBookings();
    private NoShows noShows = new NoShows();
    private Maintenance maintenance = new Maintenance();
    
    @Data
    public static class ActivateBookings {
        private Long delayMs = 30000L;
        private Integer maxRetries = 3;
        private Long timeoutMs = 30000L;
        private Boolean enabled = true;
    }
    
    @Data
    public static class NoShows {
        private Long delayMs = 60000L;
        private Integer maxRetries = 3;
        private Long timeoutMs = 30000L;
        private Boolean enabled = true;
    }
    
    @Data
    public static class Maintenance {
        private Integer historyRetentionDays = 30;
        private Integer maxHistoryRecords = 10000;
        private Boolean cleanupEnabled = true;
        private Long cleanupIntervalMs = 86400000L; // 24 hours
    }
}
