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
	private String profileImg;
	private boolean IsEdit;
	private Long contentId;
	private String contentText;
	private String contentImgPath;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
	private Date contentRegDate;
	@JsonInclude(JsonInclude.Include.NON_DEFAULT)
	private int commentsCount;
	@JsonInclude(JsonInclude.Include.NON_DEFAULT)
	private int reactionCount;

	public static ContentWithUserDto toContentWithUserDto(Long userId, Content content) {
		User user = content.getUsers();
		Long contentUserId = user.getUserId();
		String nickname = user.getNickname();
		String profileImage = user.getProfileImage();
		return ContentWithUserDto.builder()
			.userId(contentUserId)
			.name(nickname)
			.profileImg(profileImage)
			.contentId(content.getContentId())
			.contentImgPath(content.getContentImgPath())
			.contentText(content.getContentText())
			.contentRegDate(content.getContentRegDate())
			.IsEdit(userId.equals(contentUserId))
			.commentsCount(content.getComments().size())
			.reactionCount(content.getContentReactions().size())
			.build();
	}

}
