package com.k4r3l1ns.coffee_machine.config.swagger;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI setOpenApi() {
        Server devServer = new Server();
        devServer.setUrl("http://localhost:8080");
        devServer.setDescription("Server URL in Developer environment");
        Contact contact = new Contact();
        contact.setEmail("makcum.kyp4ehkob@gmail.com");
        contact.setName("k4r3l1ns");
        contact.setUrl("https://t.me/k4r3l1ns");
        Info info = new Info()
                .title("Coffee machine API")
                .version("1.0")
                .contact(contact)
                .description(
                        "This API contains endpoints which provide user with control of coffee machine"
                );
        return new OpenAPI()
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
                .components(
                        new Components().addSecuritySchemes(
                                "bearerAuth",
                                new SecurityScheme()
                                        .name("bearerAuth")
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                        )
                )
                .info(info)
                .servers(List.of(devServer));
    }
}
