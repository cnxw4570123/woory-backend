package com.woory.backend.dto;

import java.util.Set;
import java.util.stream.Collectors;

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
	private String userEmail;
	private String nickname;
	private String profileImgLink;
	private Set<GroupDto> groupList;

	public static UserResponseDto fromUser(User user) {
		return UserResponseDto.builder()
			.userId(user.getUserId())
			.userEmail(user.getEmail())
			.nickname(user.getNickname())
			.profileImgLink(user.getProfileImage())
			.groupList(user.getGroups().stream()
				.map(GroupDto::fromGroup)
				.collect(Collectors.toSet())
			)
			.build();
	}
}
