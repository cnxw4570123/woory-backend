package com.woory.backend.service;

import java.util.List;
import java.util.Optional;

import com.woory.backend.entity.GroupUser;
import com.woory.backend.error.CustomException;
import com.woory.backend.error.ErrorCode;

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

	public UserResponseDto getUserInfo(long groupId) {
		Long userId = SecurityUtil.getCurrentUserId();

		GroupUser groupUser = groupUserRepository.findGroupUserWithUserByGroupIdAndUserId(userId, groupId)
			.orElseThrow(() -> new CustomException(ErrorCode.GROUP_NOT_FOUND));

		User user = groupUser.getUser();

		return UserResponseDto.fromUserWithCurrentGroup(user, groupUser);
	}

	public void deleteAccount() {
		Long userId = SecurityUtil.getCurrentUserId();

		User user = userRepository.findByUserIdWithGroups(userId)
			.orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

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

	public void updateProfile(String filePath, String nickname) {
		Long userId = SecurityUtil.getCurrentUserId();

		User user = userRepository.findById(userId)
			.orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

		if (filePath != null) {
			user.setProfileImage(filePath);
		}
		user.setNickname(nickname);

		userRepository.save(user);
	}

}
