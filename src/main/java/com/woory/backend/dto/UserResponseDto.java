package com.woory.backend.dto;

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

	public static UserResponseDto fromUser(User user) {
		return UserResponseDto.builder()
			.userId(user.getUserId())
			.nickname(user.getNickname())
			.profileImgLink(user.getProfileImage())
			.build();
	}
}
