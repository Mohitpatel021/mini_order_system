package com.qurilo.login.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Qurilo Login Service API")
                        .description(" Small project for User related Task")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Assessment Project for QURILO")
                                .url("https://github.com/Mohitpatel021")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8082")
                                .description("Local Development Server")
                ));
    }
} 