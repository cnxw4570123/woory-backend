package com.woory.backend.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import com.woory.backend.dto.GroupInfoDto;
import com.woory.backend.dto.UserResponseDto;
import com.woory.backend.entity.User;
import com.woory.backend.repository.GroupUserRepository;
import com.woory.backend.repository.UserRepository;
import com.woory.backend.utils.SecurityUtil;

@Service
@Transactional
public class UserService {
	private final String KAKAO_KEY;
	private UserRepository userRepository;
	private GroupUserRepository groupUserRepository;

	@Autowired
	public UserService(
		@Value("${reg_info.kakao.admin-key}") String kakaoKey,
		UserRepository userRepository,
		GroupUserRepository groupUserRepository) {
		this.KAKAO_KEY = kakaoKey;
		this.userRepository = userRepository;
		this.groupUserRepository = groupUserRepository;
	}

	public UserResponseDto getUserInfo() {
		Long userId = SecurityUtil.getCurrentUserId();

		User user = userRepository.findByUserIdWithGroups(userId)
			.orElseThrow(() -> new RuntimeException("회원 정보 없음"));

		List<GroupInfoDto> myGroup = groupUserRepository.findMyGroupInfoDto(userId);

		return UserResponseDto.fromUserAndGroups(user, myGroup);
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
