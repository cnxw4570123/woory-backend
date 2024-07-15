package com.woory.backend.config;

import org.springdoc.core.properties.SwaggerUiConfigParameters;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

@Configuration
public class SwaggerConfig {
	@Bean
	public OpenAPI openAPI() {
		String cookie = "cookie";
		SecurityRequirement securityRequirement = new SecurityRequirement().addList(cookie);
		Components components = new Components().addSecuritySchemes(cookie,
			new SecurityScheme()
				.type(SecurityScheme.Type.APIKEY)
				.in(SecurityScheme.In.COOKIE)
				.name("AccessToken")
		);
		return new OpenAPI()
			.components(new Components())
			.addSecurityItem(securityRequirement)
			.components(components)
			.info(apiInfo());
	}

	private Info apiInfo() {
		return new Info()
			.title("Woory")
			.description("백엔드 API")
			.version("1.0.0");
	}
}
