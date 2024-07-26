package com.woory.backend.dto;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.woory.backend.entity.Content;
import com.woory.backend.entity.User;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ContentWithUserDto {
	private Long userId;
	private String name;
	private String profileUrl;
	private boolean IsEdit;
	private Long contentId;
	private String contentText;
	private String contentImgPath;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
	private Date contentRegDate;
	private Integer commentsCount;
	private Integer reactionCount;

	public static ContentWithUserDto toContentWithUserDto(Long userId, Content content) {
		User user = content.getUsers();
		Long contentUserId = user.getUserId();
		String nickname = user.getNickname();
		String profileImage = user.getProfileImage();
		int commentsCount = content.getComments().size();
		int size = content.getContentReactions().size();
		return ContentWithUserDto.builder()
			.userId(contentUserId)
			.name(nickname)
			.profileUrl(profileImage)
			.contentId(content.getContentId())
			.contentImgPath(content.getContentImgPath())
			.contentText(content.getContentText())
			.contentRegDate(content.getContentRegDate())
			.IsEdit(userId.equals(contentUserId))
			.commentsCount(commentsCount)
			.reactionCount(size)
			.build();
	}

	public static ContentWithUserDto toContentWithUserDtoWithoutCounts(Long userId, Content content) {
		User user = content.getUsers();
		Long contentUserId = user.getUserId();
		String nickname = user.getNickname();
		String profileImage = user.getProfileImage();
		return ContentWithUserDto.builder()
			.userId(contentUserId)
			.name(nickname)
			.profileUrl(profileImage)
			.contentId(content.getContentId())
			.contentImgPath(content.getContentImgPath())
			.contentText(content.getContentText())
			.contentRegDate(content.getContentRegDate())
			.IsEdit(userId.equals(contentUserId))
			.build();
	}

}
