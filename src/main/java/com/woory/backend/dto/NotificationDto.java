package com.woory.backend.dto;

import java.util.Date;

import com.woory.backend.entity.NotificationType;

public interface NotificationDto {
	Long getNotificationId();

	Long getGroupId();

	Date getTopicDate();

	Long getTopicId();

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

	Date getIssueDate();

	String getAuthor();

}
