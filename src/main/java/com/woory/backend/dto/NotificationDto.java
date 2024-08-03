package com.woory.backend.dto;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.woory.backend.entity.NotificationType;

@JsonInclude(JsonInclude.Include.NON_NULL)
public interface NotificationDto {
	Long getGroupId();

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
	Date getTopicDate();

	String getTopicTitle();

	Long getContentUserId();

	Long getContentId();

	Long getCommentUserId();

	Long getCommentId();

	Long getReplyUserId();

	Long getReplyId();

	Long getReactionUserId();

	Long getReactionId();

	Long getUserId();

	NotificationType getNotificationType();

	String getAuthor();



}
