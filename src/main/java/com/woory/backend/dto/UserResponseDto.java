package com.woory.backend.dto;

import com.woory.backend.entity.GroupStatus;
import com.woory.backend.entity.GroupUser;
import com.woory.backend.entity.User;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class UserResponseDto {
	private long userId;
	private String nickname;
	private String profileImgLink;
	private boolean IsHouseholder;

	public static UserResponseDto fromUserWithCurrentGroup(User user, GroupUser groupUser) {
		return UserResponseDto.builder()
			.userId(user.getUserId())
			.nickname(user.getNickname())
			.profileImgLink(user.getProfileImage())
			.IsHouseholder(groupUser.getStatus().equals(GroupStatus.GROUP_LEADER))
			.build();
	}

	public static UserResponseDto fromUser(User user) {
		return UserResponseDto.builder()
			.userId(user.getUserId())
			.nickname(user.getNickname())
			.profileImgLink(user.getProfileImage())
			.build();
	}
}
