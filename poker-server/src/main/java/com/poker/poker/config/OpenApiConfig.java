package com.poker.poker.config;

import com.poker.poker.config.constants.AppConstants;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.servers.Server;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@OpenAPIDefinition(
    servers = {
      @Server(url = "http://poker-testing.ngrok.io"),
      @Server(url = "http://bugsauce-poker.eba-cbxu2gew.us-east-1.elasticbeanstalk.com/")
    })
@Configuration
@AllArgsConstructor
public class OpenApiConfig {

  private final AppConstants appConstants;

  @Bean
  public OpenAPI customConfiguration() {
    return new OpenAPI()
        .components(
            new Components()
                .addSecuritySchemes(
                    appConstants.getSecurityScheme(),
                    new SecurityScheme()
                        .type(SecurityScheme.Type.HTTP)
                        .scheme(appConstants.getSecurityScheme())
                        .bearerFormat(appConstants.getBearerFormat())))
        .info(
            new Info()
                .title(appConstants.getSwaggerTitle())
                .description(appConstants.getSwaggerDescription()))
        .security(appConstants.getSecurityRequirements());
  }
}
