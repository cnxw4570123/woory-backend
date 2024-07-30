package com.woory.backend.dto;

import com.woory.backend.entity.User;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserDto {
	private String username;
	private Long userId;
	private String nickname;
	private String role;

	public static UserDto fromUser(User user) {
		return UserDto.builder()
			.userId(user.getUserId())
			.username(user.getUsername())
			.nickname(user.getNickname())
			.role(user.getRole())
			.build();
	}
}
