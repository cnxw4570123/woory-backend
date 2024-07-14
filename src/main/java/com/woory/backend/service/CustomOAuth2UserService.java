package com.woory.backend.service;

import java.util.Optional;

import com.woory.backend.dto.CustomOAuth2User;
import com.woory.backend.dto.KakaoResponse;
import com.woory.backend.dto.NaverResponse;
import com.woory.backend.dto.OAuth2Response;
import com.woory.backend.dto.UserDto;
import com.woory.backend.entity.User;
import com.woory.backend.repository2.UserRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

	private static final Logger log = LoggerFactory.getLogger(CustomOAuth2UserService.class);
	private final UserRepository userRepository;

	public CustomOAuth2UserService(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	@Override
	public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
		OAuth2User oAuth2User = super.loadUser(userRequest);
		log.info("oAuth2User = {}", oAuth2User.getAttributes());

		String registration = userRequest.getClientRegistration().getRegistrationId();
		OAuth2Response oAuth2Response;
		if (registration.equals("naver")) {
			oAuth2Response = new NaverResponse(oAuth2User.getAttributes());
		} else if (registration.equals("kakao")) {
			oAuth2Response = new KakaoResponse(oAuth2User.getAttributes());
		} else {
			return null;
		}
		//구현
		String username = oAuth2Response.getProvider() + " " + oAuth2Response.getProviderId();

		User byUsername = userRepository.findByUsername(username)
			.orElseGet(() -> {
				User user = new User();
				user.setUsername(username);
				user.setEmail(oAuth2Response.getEmail());
				user.setProfileImage(oAuth2Response.getProfileImage());
				user.setRole("ROLE_USER");
				user.setNickname(oAuth2Response.getName());
				return user;
			});

		byUsername.setUsername(username);
		byUsername.setEmail(oAuth2Response.getEmail());
		byUsername.setProfileImage(oAuth2Response.getProfileImage());
		return new CustomOAuth2User(UserDto.fromUser(userRepository.save(byUsername)));
	}

}
