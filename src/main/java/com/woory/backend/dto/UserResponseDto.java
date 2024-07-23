package com.woory.backend.dto;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
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
	@JsonProperty("isHouseholder")
	private boolean isHouseHolder;

	public static UserResponseDto fromUserWithCurrentGroup(User user, GroupUser groupUser) {
		return UserResponseDto.builder()
			.userId(user.getUserId())
			.nickname(user.getNickname())
			.profileImgLink(user.getProfileImage())
			.isHouseHolder(groupUser.getStatus().equals(GroupStatus.GROUP_LEADER))
			.build();
	}
}
