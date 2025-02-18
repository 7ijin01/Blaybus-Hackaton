package com.blaybus.global.config;

import io.swagger.v3.core.util.PrimitiveType;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        Info info = new Info()
                .version("v1.0")
                .title("Glow Up API");
        return new OpenAPI()
                .info(info);
    }
    @PostConstruct
    public void setup() {
        PrimitiveType.enablePartialTime();
    }

}

