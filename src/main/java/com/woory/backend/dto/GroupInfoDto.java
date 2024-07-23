package com.woory.backend.dto;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.woory.backend.entity.GroupStatus;

import lombok.Getter;


@Getter
public class GroupInfoDto {
	private Long groupId;
	private String groupName;
	private String groupImage;
	@JsonIgnore
	private GroupStatus status;

	public GroupInfoDto(Long groupId, String groupName, String groupImage, GroupStatus groupStatus) {
		this.groupId = groupId;
		this.groupName = groupName;
		this.groupImage = groupImage;
		this.status = groupStatus;
	}

	public static Map<GroupStatus, List<GroupInfoDto>> toSeparatedByStatus(List<GroupInfoDto> groupInfoList) {
		return groupInfoList.stream()
			.collect(Collectors.groupingBy(GroupInfoDto::getStatus));
	}
}
