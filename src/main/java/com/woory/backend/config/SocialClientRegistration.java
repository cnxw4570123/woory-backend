package com.woory.backend.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;

@Configuration
public class SocialClientRegistration {

	@Bean
	@Autowired
	@Primary
	public ClientRegistration kakaoClientRegistration(
		@Value("${reg_info.kakao.client-id}") String clientId,
		@Value("${reg_info.kakao.client-secret}") String clientSecret,
		@Value("${reg_info.kakao.redirect-uri}") String redirectURI,
		@Value("${reg_info.kakao.scope}") String scope
	) {
		return ClientRegistration.withRegistrationId("kakao")
			.clientId(clientId)
			.clientSecret(clientSecret)
			.redirectUri(redirectURI)
			.authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
			.scope(scope.split(", "))
			// 카카오의 경우 client_secret_post 없으면 오류 발생
			.clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_POST)
			.authorizationUri("https://kauth.kakao.com/oauth/authorize")
			.tokenUri("https://kauth.kakao.com/oauth/token")
			.userInfoUri("https://kapi.kakao.com/v2/user/me")
			.userNameAttributeName("kakao_account")
			.build();
	}

	@Bean
	@Autowired
	@Qualifier("naverClientRegistration")
	public ClientRegistration naverClientRegistration(
		@Value("${reg_info.naver.client-id}") String clientId,
		@Value("${reg_info.naver.client-secret}") String clientSecret,
		@Value("${reg_info.naver.redirect-uri}") String redirectUri,
		@Value("${reg_info.naver.scope}") String scope
	) {
		return ClientRegistration.withRegistrationId("naver")
			.clientId(clientId)
			.clientSecret(clientSecret)
			.redirectUri(redirectUri)
			.authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
			.scope(scope.split(", "))
			.authorizationUri("https://nid.naver.com/oauth2.0/authorize")
			.tokenUri("https://nid.naver.com/oauth2.0/token")
			.userInfoUri("https://openapi.naver.com/v1/nid/me")
			.userNameAttributeName("response")
			.build();
	}
}
