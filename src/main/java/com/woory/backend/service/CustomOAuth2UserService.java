package com.woory.backend.service;

import java.net.URI;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.woory.backend.dto.CustomOAuth2User;
import com.woory.backend.dto.KakaoResponse;
import com.woory.backend.dto.NaverResponse;
import com.woory.backend.dto.OAuth2Response;
import com.woory.backend.dto.UserDto;
import com.woory.backend.entity.User;
import com.woory.backend.error.CustomException;
import com.woory.backend.error.ErrorCode;
import com.woory.backend.repository.UserRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

	private static final Logger log = LoggerFactory.getLogger(CustomOAuth2UserService.class);
	private final UserRepository userRepository;
	private final String userDefaultImg;
	private final AwsService awsService;
	private final RestClient restClient;

	@Autowired
	public CustomOAuth2UserService(UserRepository userRepository,
		AwsService awsService,
		RestClient restClient,
		@Value("${service.default.profileImg}") String profileImg
	) {
		this.restClient = restClient;
		this.userRepository = userRepository;
		this.userDefaultImg = profileImg;
		this.awsService = awsService;
	}

	@Override
	public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
		OAuth2User oAuth2User = super.loadUser(userRequest);
		log.info("oAuth2User = {}", oAuth2User.getAttributes());
		String accessToken = userRequest.getAccessToken().getTokenValue();
		ClientRegistration clientRegistration = userRequest.getClientRegistration();

		String registration = userRequest.getClientRegistration().getRegistrationId();
		OAuth2Response oAuth2Response;
		if (registration.equals("naver")) {
			oAuth2Response = new NaverResponse(oAuth2User.getAttributes());
		} else if (registration.equals("kakao")) {
			oAuth2Response = new KakaoResponse(oAuth2User.getAttributes());
		} else {
			return null;
		}

		oAuth2LogoutImmediately(accessToken, clientRegistration);

		//구현
		String username = oAuth2Response.getProvider() + " " + oAuth2Response.getProviderId();

		User byUsername = userRepository.findByUsername(username)
			.orElseGet(() -> {
				User user = new User();
				user.setUsername(username);
				// user.setEmail(oAuth2Response.getEmail());
				user.setProfileImage(oAuth2Response.getProfileImage().isEmpty() ? userDefaultImg :
					awsService.saveImage(oAuth2Response.getProfileImage()));
				user.setRole("ROLE_USER");
				user.setNickname(oAuth2Response.getName());
				return user;
			});

		byUsername.setUsername(username);
		// byUsername.setEmail(oAuth2Response.getEmail());
		return new CustomOAuth2User(UserDto.fromUser(userRepository.save(byUsername)));
	}

	private void oAuth2LogoutImmediately(String accessToken, ClientRegistration registration) {
		if (registration.getRegistrationId().equals("naver")) {
			MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
			params.add("grant_type", "delete"); // 토큰 삭제 시 delete
			params.add("client_id", registration.getClientId());
			params.add("client_secret", registration.getClientSecret());
			params.add("access_token", accessToken);
			URI uri = UriComponentsBuilder.fromUriString("https://nid.naver.com")
				.path("/oauth2.0/token")
				.encode()
				.build()
				.toUri();

			String response = restClient.post()
				.uri(uri)
				.body(params)
				.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
				.exchange((req, res) -> {
					if (res.getStatusCode().isError()) {
						throw new CustomException(ErrorCode.SOCIAL_LOGIN_ERROR);
					}
					return new ObjectMapper().readTree(res.getBody()).toString();
				});

			log.info("response = {}", response);
			return;
		}

		if (registration.getRegistrationId().equals("kakao")) {
			URI uri = UriComponentsBuilder.fromUriString("https://kapi.kakao.com")
				.path("/v1/user/logout")
				.encode()
				.build()
				.toUri();

			String response = restClient.post()
				.uri(uri)
				.headers(httpHeaders -> {
					httpHeaders.add(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
					httpHeaders.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE);
				}).exchange((req, res) -> {
					if (res.getStatusCode().isError()) {
						throw new CustomException(ErrorCode.SOCIAL_LOGIN_ERROR);
					}
					return new ObjectMapper().readTree(res.getBody()).toString();
				});

			log.info("response = {}", response);
		}
	}

}
