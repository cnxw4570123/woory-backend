package com.woory.backend.service;

import java.util.List;

import com.woory.backend.dto.UserMyPageResponseDto;
import com.woory.backend.entity.Group;
import com.woory.backend.entity.GroupStatus;
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

import com.woory.backend.dto.UserResponseDto;
import com.woory.backend.entity.User;
import com.woory.backend.repository.GroupRepository;
import com.woory.backend.repository.GroupUserRepository;
import com.woory.backend.repository.UserRepository;
import com.woory.backend.utils.SecurityUtil;

@Service
@Transactional
public class UserService {
	private final String KAKAO_KEY;
	private final UserRepository userRepository;
	private final GroupUserRepository groupUserRepository;
	private final GroupRepository groupRepository;

	@Autowired
	public UserService(
		@Value("${reg_info.kakao.admin-key}") String kakaoKey,
		UserRepository userRepository,
		GroupUserRepository groupUserRepository,
		GroupRepository groupRepository) {
		this.KAKAO_KEY = kakaoKey;
		this.userRepository = userRepository;
		this.groupUserRepository = groupUserRepository;
		this.groupRepository = groupRepository;
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
		};


		User user = groupUser.getUser();

		return UserMyPageResponseDto.fromUserWithCurrentGroup(user, groupUser, isLastMember);
	}

	public void deleteAccount() {
		Long userId = SecurityUtil.getCurrentUserId();

		User user = userRepository.findByUserIdWithGroupUsers(userId)
			.orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));


		if (user.getUsername().startsWith("kakao")) {
			String kakaoId = user.getUsername().split("kakao ")[1];
			unlinkKakao(kakaoId);
		}

		List<Group> groups = groupRepository.findGroupHasUser(user);

		// 내가 속한 그룹 순회
		for(Group group: groups){
			List<GroupUser> groupUsers = group.getGroupUsers();

			// 내 그룹 기록 중
			for(GroupUser userHistory :user.getGroupUsers()){
				if(!groupUsers.contains(userHistory)){
					continue;
				}
				// 속한 그룹과 일치하는 기록이 있으면

				// 멤버면
				if(userHistory.getStatus().equals(GroupStatus.MEMBER)){
					groupUserRepository.delete(userHistory);
					break;
				}

				// 이 시점부터는 가장
				// 나 말고 다른 유저가 있으면 -> 다음 가장 후보에게 권한 승계
				if(groupUsers.size() > 1){
					GroupUser nextHouseHolder = groupUsers.get(1);
					nextHouseHolder.setStatus(GroupStatus.GROUP_LEADER);
					groupUserRepository.delete(userHistory);
					groupUserRepository.save(nextHouseHolder);
				} else {
					// 내가 가장이고 나만 남은 상태
					groupUserRepository.delete(userHistory);
					groupRepository.delete(group);
				}
			}
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
