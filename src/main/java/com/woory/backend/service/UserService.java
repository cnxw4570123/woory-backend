package com.woory.backend.service;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import com.woory.backend.dto.UserMyPageResponseDto;
import com.woory.backend.entity.Group;
import com.woory.backend.entity.GroupStatus;
import com.woory.backend.entity.GroupUser;
import com.woory.backend.error.CustomException;
import com.woory.backend.error.ErrorCode;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import com.woory.backend.dto.UserResponseDto;
import com.woory.backend.entity.User;
import com.woory.backend.repository.GroupRepository;
import com.woory.backend.repository.GroupUserRepository;
import com.woory.backend.repository.UserRepository;
import com.woory.backend.utils.SecurityUtil;

@Service
@Transactional
public class UserService {
	private static final Logger log = LoggerFactory.getLogger(UserService.class);
	private final String KAKAO_KEY;
	private final UserRepository userRepository;
	private final GroupUserRepository groupUserRepository;
	private final GroupRepository groupRepository;
	private final RestClient restClient;

	@Autowired
	public UserService(
		@Value("${reg_info.kakao.admin-key}") String kakaoKey,
		UserRepository userRepository,
		GroupUserRepository groupUserRepository,
		GroupRepository groupRepository,
		RestClient restClient) {
		this.KAKAO_KEY = kakaoKey;
		this.userRepository = userRepository;
		this.groupUserRepository = groupUserRepository;
		this.groupRepository = groupRepository;
		this.restClient = restClient;
	}

	public UserResponseDto getMyInfo() {
		User user = userRepository.findById(SecurityUtil.getCurrentUserId())
			.orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
		return UserResponseDto.fromUser(user);
	}

	public UserMyPageResponseDto getUserInfo(long groupId) {
		Long userId = SecurityUtil.getCurrentUserId();

		GroupUser groupUser = groupUserRepository.findGroupUserWithUserByGroupIdAndUserId(userId, groupId)
			.orElseThrow(() -> new CustomException(ErrorCode.GROUP_NOT_FOUND));
		List<GroupUser> allByGroupGroupId = groupUserRepository.findAllByGroup_GroupId(groupId);
		int size = allByGroupGroupId.size();
		boolean isLastMember = false;
		if (size == 1) {
			isLastMember = true;
		}
		;

		User user = groupUser.getUser();

		return UserMyPageResponseDto.fromUserWithCurrentGroup(user, groupUser, isLastMember);
	}

	public void deleteAccount() {
		User user = handleGroupWhileDeletingUser();

		if (user.getUsername().startsWith("kakao")) {
			String kakaoId = user.getUsername().split("kakao ")[1];
			unlinkKakao(kakaoId);
		}
	}

	@Transactional
	protected User handleGroupWhileDeletingUser() {
		Long userId = SecurityUtil.getCurrentUserId();

		User user = userRepository.findByUserIdWithGroupUsers(userId)
			.orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

		List<Group> groups = groupRepository.findGroupHasUser(user);
		
		// 내가 속한 그룹 순회
		for (Group group : groups) {
			List<GroupUser> groupUsers = group.getGroupUsers();

			// 내 그룹 기록 중
			List<GroupUser> userGroups = new ArrayList<>(user.getGroupUsers());
			for (GroupUser userHistory : userGroups) {
				if (!groupUsers.contains(userHistory)) {
					continue;
				}
				// 속한 그룹과 일치하는 기록이 있으면

				user.getGroupUsers().remove(userHistory);
				userHistory.setUser(null);
				// 멤버면
				if (userHistory.getStatus().equals(GroupStatus.MEMBER)) {
					// groupUserRepository.delete(userHistory); 삭제 부분 제거
					userHistory.setGroup(null);
					break;
				}

				// 이 시점부터는 가장
				// 나 말고 다른 유저가 있으면 -> 다음 가장 후보에게 권한 승계
				if (groupUsers.size() > 1) {
					GroupUser nextHouseHolder = groupUsers.get(1);
					nextHouseHolder.setStatus(GroupStatus.GROUP_LEADER);
					userHistory.setGroup(null);
					// groupUserRepository.delete(userHistory); 삭제 부분 제거
					groupUserRepository.save(nextHouseHolder);
				} else {
					// 내가 가장이고 나만 남은 상태
					// groupUserRepository.delete(userHistory); 삭제 부분 제
					groupRepository.delete(group);
				}
			}
		}

		// // orphanremoval 활용
		// user.getGroupUsers().clear();
		userRepository.delete(user);
		return user;
	}


	private void unlinkKakao(String kakaoId) {
		URI uri = UriComponentsBuilder.fromHttpUrl("https://kapi.kakao.com/v1/user/unlink")
			.build()
			.toUri();

		MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
		body.add("target_id_type", "user_id");
		body.add("target_id", kakaoId);

		String authorization = restClient.post()
			.uri(uri)
			.header(HttpHeaders.AUTHORIZATION, "KakaoAK " + KAKAO_KEY)
			.body(body)
			.retrieve()
			.body(String.class);
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
