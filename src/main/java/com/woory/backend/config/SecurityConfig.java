package com.woory.backend.config;

import com.woory.backend.service.CustomOAuth2UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

	private final CustomOAuth2UserService oAuth2UserService;
	private final ClientRegistrationRepository clientRegistrationRepository;

	@Autowired
	public SecurityConfig(CustomOAuth2UserService oAuth2UserService,
		ClientRegistrationRepository clientRegistrationRepository) {
		this.oAuth2UserService = oAuth2UserService;
		this.clientRegistrationRepository = clientRegistrationRepository;
	}

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

		http
			.csrf((csrf) -> csrf.disable());
		http
			.formLogin((login) -> login.disable());
		http
			.httpBasic((httpBasic) -> httpBasic.disable());
		http
			.oauth2Login((oauth2) -> oauth2
				.loginPage("/login")
				.clientRegistrationRepository(clientRegistrationRepository)
				.userInfoEndpoint(userInfoEndpointConfig ->
					userInfoEndpointConfig.userService(oAuth2UserService))); //서버전달체계만들어줌
		http
			.authorizeHttpRequests((auth) -> auth
				.requestMatchers("/", "/oauth2/**", "/login/**", "/error").permitAll()  //에러 페이지 허용
				.anyRequest().authenticated());

		return http.build();
	}
}
