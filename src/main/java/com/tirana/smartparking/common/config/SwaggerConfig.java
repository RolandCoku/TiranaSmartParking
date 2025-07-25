package com.tirana.smartparking.common.config;

import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Smart Parking System API")
                        .version("1.0.0")
                        .description("API documentation for the Smart Parking System")
                        .contact(new Contact()
                                .name("SmartParking Team")
                                .email("support@smartparking.com")
                                .url("https://smartparking.com"))
                        .license(new License().name("Apache 2.0").url("https://springdoc.org")));
    }
}
