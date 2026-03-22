package com.example.order.infrastructure.openapi;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI productApiOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Order API")
                        .description("Spring Boot Hexagonal API Order Service documentation")
                        .version("1.0.0"));
    }
}
