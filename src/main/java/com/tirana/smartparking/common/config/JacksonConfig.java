package com.tirana.smartparking.common.config;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@Configuration
public class JacksonConfig {

    /**
     * Custom deserializer for ZonedDateTime that can handle multiple formats
     * including formats without timezone information (defaults to system timezone)
     */
    public static class FlexibleZonedDateTimeDeserializer extends JsonDeserializer<ZonedDateTime> {
        
        private static final DateTimeFormatter[] FORMATTERS = {
            // Full ISO format with timezone
            DateTimeFormatter.ISO_ZONED_DATE_TIME,
            // Format without seconds: 2025-09-18T10:51+02:00
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mmXXX"),
            // Format without seconds and timezone: 2025-09-18T10:51 (defaults to system timezone)
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm"),
            // Format with seconds but no timezone: 2025-09-18T10:51:00 (defaults to system timezone)
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"),
            // Format with milliseconds but no timezone: 2025-09-18T10:51:00.000 (defaults to system timezone)
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS")
        };

        @Override
        public ZonedDateTime deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            String dateString = p.getText();
            
            // Try each formatter until one works
            for (DateTimeFormatter formatter : FORMATTERS) {
                try {
                    return ZonedDateTime.parse(dateString, formatter);
                } catch (DateTimeParseException e) {
                    // Continue to next formatter
                }
            }
            
            // If none of the formatters work, throw a more descriptive error
            throw new IOException("Unable to parse date string '" + dateString + 
                "'. Supported formats are: " +
                "yyyy-MM-dd'T'HH:mm:ssXXX (e.g., 2025-09-18T10:51:00+02:00), " +
                "yyyy-MM-dd'T'HH:mmXXX (e.g., 2025-09-18T10:51+02:00), " +
                "yyyy-MM-dd'T'HH:mm:ss (e.g., 2025-09-18T10:51:00), " +
                "yyyy-MM-dd'T'HH:mm (e.g., 2025-09-18T10:51)");
        }
    }

    @Bean
    @Primary
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        
        // Register JavaTimeModule for handling Java 8 time types
        JavaTimeModule javaTimeModule = new JavaTimeModule();
        
        // Add our custom ZonedDateTime deserializer
        javaTimeModule.addDeserializer(ZonedDateTime.class, new FlexibleZonedDateTimeDeserializer());
        
        mapper.registerModule(javaTimeModule);
        
        // Configure date/time serialization
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        
        return mapper;
    }
}
