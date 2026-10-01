package com.schooltech.sms.configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

        @Bean
        public OpenAPI schoolTechOpenAPI() {

            String jwtScheme = "bearerAuth";
            String tenantScheme = "tenantId";

            return new OpenAPI()

                    // API information
                    .info(new Info()
                            .title("EduEmissary APIs")
                            .description("API documentation for SchoolTech")
                            .version("1.0.0"))

                    // Apply JWT + Tenant ID globally
                    .addSecurityItem(new SecurityRequirement()
                            .addList(jwtScheme)
                            .addList(tenantScheme))

                    // Define security schemes
                    .components(new Components()

                            // JWT
                            .addSecuritySchemes(jwtScheme,
                                    new SecurityScheme()
                                            .name("Authorization")
                                            .type(SecurityScheme.Type.HTTP)
                                            .scheme("bearer")
                                            .bearerFormat("JWT"))

                            // Tenant ID
                            .addSecuritySchemes(tenantScheme,
                                    new SecurityScheme()
                                            .name("X-Tenant-ID")
                                            .type(SecurityScheme.Type.APIKEY)
                                            .in(SecurityScheme.In.HEADER))
                    );
        }
    }