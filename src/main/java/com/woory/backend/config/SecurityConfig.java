package com.woory.backend.config;

import java.util.List;

import com.woory.backend.filter.JWTFilter;
import com.woory.backend.service.CustomOAuth2UserService;
import com.woory.backend.utils.JWTUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
@EnableWebSecurity(debug = true)
public class SecurityConfig {

	private final CustomOAuth2UserService oAuth2UserService;
	private final ClientRegistrationRepository clientRegistrationRepository;
	private final JWTUtil jwtUtil;
	private final CustomSuccessHandler successHandler;

	@Autowired
	public SecurityConfig(CustomOAuth2UserService oAuth2UserService,
		ClientRegistrationRepository clientRegistrationRepository, JWTUtil jwtUtil,
		CustomSuccessHandler successHandler) {
		this.oAuth2UserService = oAuth2UserService;
		this.clientRegistrationRepository = clientRegistrationRepository;
		this.jwtUtil = jwtUtil;
		this.successHandler = successHandler;
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
			.addFilterBefore(new JWTFilter(jwtUtil), UsernamePasswordAuthenticationFilter.class)
			.oauth2Login((oauth2) -> oauth2
				.loginPage("/login")
				.clientRegistrationRepository(clientRegistrationRepository)
				.userInfoEndpoint(userInfoEndpointConfig ->
					userInfoEndpointConfig.userService(oAuth2UserService))
				.successHandler(successHandler) // 로그인 성공 시에 응답에 토큰 넣어줌
			); //서버전달체계만들어줌

		http
			.sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
			.authorizeHttpRequests((auth) -> auth
				.requestMatchers("/", "/oauth2/**", "/login/**", "/error").permitAll()  //에러 페이지 허용
				.anyRequest().authenticated());

		return http.build();
	}
}

