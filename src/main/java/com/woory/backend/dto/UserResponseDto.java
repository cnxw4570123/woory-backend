package com.woory.backend.dto;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.woory.backend.entity.GroupStatus;
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
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private Map<GroupStatus, List<GroupInfoDto>> byGroupStatus;

	public static UserResponseDto fromUserAndGroups(User user, List<GroupInfoDto> groupList) {
		return UserResponseDto.builder()
			.userId(user.getUserId())
			.nickname(user.getNickname())
			.byGroupStatus(GroupInfoDto.toSeparatedByStatus(groupList))
			.profileImgLink(user.getProfileImage())
			.build();
	}
}
