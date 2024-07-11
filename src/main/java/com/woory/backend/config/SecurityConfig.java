package com.woory.backend.config;

import com.woory.backend.service.CustomOAuth2UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final CustomOAuth2UserService oAuth2UserService;

    public SecurityConfig(CustomOAuth2UserService oAuth2UserService) {
        this.oAuth2UserService = oAuth2UserService;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .csrf((csrf)->csrf.disable());
        http
                .formLogin((login)->login.disable());
        http
                .httpBasic((httpBasic)->httpBasic.disable());
        http
                .oauth2Login((oauth2)->oauth2
                        .loginPage("/login")
                        .userInfoEndpoint(userInfoEndpointConfig ->
                        userInfoEndpointConfig.userService(oAuth2UserService))); //서버전달체계만들어줌
        http
                .authorizeHttpRequests((auth)->auth
                        .requestMatchers("/","/oauth2/**","/login/**").permitAll()  //허용
                        .anyRequest().authenticated());

        return http.build();
    }
}
