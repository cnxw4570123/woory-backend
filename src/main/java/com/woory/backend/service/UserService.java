package com.woory.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import com.woory.backend.dto.UserResponseDto;
import com.woory.backend.entity.User;
import com.woory.backend.repository2.UserRepository;
import com.woory.backend.utils.SecurityUtil;

import lombok.AllArgsConstructor;

@Service
@Transactional
public class UserService {
	private final String KAKAO_KEY;
	private UserRepository userRepository;

	@Autowired
	public UserService(@Value("${reg_info.kakao.admin-key}") String kakaoKey, UserRepository userRepository) {
		this.KAKAO_KEY = kakaoKey;
		this.userRepository = userRepository;
	}

	public UserResponseDto getUserInfo() {
		Long userId = SecurityUtil.getCurrentUserId();

		User user = userRepository.findByUserIdWithGroups(userId)
			.orElseThrow(() -> new RuntimeException("회원 정보 없음"));

		return UserResponseDto.fromUser(user);
	}

	public void deleteAccount() {
		Long userId = SecurityUtil.getCurrentUserId();

		User user = userRepository.findByUserIdWithGroups(userId)
			.orElseThrow(() -> new RuntimeException("회원 정보 없음"));

		if (user.getUsername().startsWith("kakao")) {
			String kakaoId = user.getUsername().split("kakao ")[1];
			unlinkKakao(kakaoId);
		}

		userRepository.delete(user);
	}

	private void unlinkKakao(String kakaoId) {
		WebClient kakaoClient = WebClient.builder()
			.baseUrl("https://kapi.kakao.com/v1/user/unlink")
			.build();

		MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
		body.add("target_id_type", "user_id");
		body.add("target_id", kakaoId);

		String authorization = kakaoClient.post()
			.header("Authorization", "KakaoAK " + KAKAO_KEY)
			.contentType(MediaType.APPLICATION_FORM_URLENCODED)
			.body(BodyInserters.fromFormData(body))
			.retrieve()
			.bodyToMono(String.class)
			.block();
	}

}
