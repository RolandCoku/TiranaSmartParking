package com.tirana.smartparking;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@OpenAPIDefinition(
        info = @Info(title = "SmartParking API", version = "1.0", description = "API documentation for SmartParking"),
        servers = @Server(url = "/", description = "Default Server URL")
)

@SpringBootApplication
@EnableJpaAuditing
public class ParkingApplication {
    public static void main(String[] args) {
        SpringApplication.run(ParkingApplication.class, args);
    }

}
