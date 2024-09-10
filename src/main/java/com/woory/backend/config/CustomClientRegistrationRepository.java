package com.woory.backend.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;

@Configuration
public class CustomClientRegistrationRepository {

	private final ClientRegistration kakaoClientRegistration;
	private final ClientRegistration naverClientRegistration;

	@Autowired
	public CustomClientRegistrationRepository(
		ClientRegistration kakaoClientRegistration,
		@Qualifier("naverClientRegistration") ClientRegistration naverClientRegistration) {
		this.kakaoClientRegistration = kakaoClientRegistration;
		this.naverClientRegistration = naverClientRegistration;
	}

	@Bean
	public ClientRegistrationRepository clientRegistrationRepository() {
		return new InMemoryClientRegistrationRepository(kakaoClientRegistration, naverClientRegistration);
	}
}
