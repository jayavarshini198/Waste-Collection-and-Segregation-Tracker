package com.example.WastePickup.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.Contact;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "WastePickup API",
                version = "1.0",
                description = "REST API for zone-based waste collection schedules and household segregation score tracking.",
                contact = @Contact(name = "WastePickup Project")
        )
)
public class OpenApiConfig {
}
