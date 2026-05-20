package com.project.ecommerce.config;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.List;


@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI customOpenAPI(){
        final String securitySchemeName = "bearerAuth";
        Server server = new Server();
        server.setUrl("/");
        server.setDescription("Default Server URL");

        return new OpenAPI()
                .servers(List.of(server))
                .info(new Info()
                        .title("Ecommerce API")
                        .version("1.0")
                        .description("Enterprise Ecommerce Backend APIs with JWT Authentication")
                        .contact(new Contact().name("Priyakant Kumar"))
                )
                .addSecurityItem(new SecurityRequirement()
                                .addList(securitySchemeName)
                )
                .components(
                        new io.swagger.v3.oas.models.Components()
                        .addSecuritySchemes(securitySchemeName,
                                new SecurityScheme()
                                        .name(securitySchemeName)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("Enter JWT token with Bearer prefix")
                                )
                );
    }
}
