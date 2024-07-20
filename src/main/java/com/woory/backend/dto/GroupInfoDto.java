package com.woory.backend.dto;

import lombok.Getter;


@Getter
public class GroupInfoDto {
	private Long groupId;
	private String groupName;
	private String groupImage;

	public GroupInfoDto(Long groupId, String groupName, String groupImage) {
		this.groupId = groupId;
		this.groupName = groupName;
		this.groupImage = groupImage;
	}
}
