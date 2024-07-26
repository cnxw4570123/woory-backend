package com.woory.backend.dto;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.woory.backend.entity.Content;
import com.woory.backend.entity.Topic;
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
public class ContentWithUserAndTopicDto {
	private Long topicId;
	private String topicContent;
	private Long userId;
	private String name;
	private String profileUrl;
	private boolean IsEdit;
	private Long contentId;
	private String contentText;
	private String contentImgPath;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
	private Date contentRegDate;

	public static ContentWithUserAndTopicDto fromTopicAndContent(Long curUserId, Content content, Topic t) {
		User user = content.getUsers();
		Long userId1 = user.getUserId();
		String nickname = user.getNickname();
		String profileImage = user.getProfileImage();
		return ContentWithUserAndTopicDto.builder()
			.topicId(t.getTopicId())
			.topicContent(t.getTopicContent())
			.userId(userId1)
			.name(nickname)
			.profileUrl(profileImage)
			.contentImgPath(content.getContentImgPath())
			.contentText(content.getContentText())
			.contentRegDate(content.getContentRegDate())
			.IsEdit(userId1.equals(curUserId))
			.build();


	}
}
