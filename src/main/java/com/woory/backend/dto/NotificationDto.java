package com.woory.backend.dto;

import java.util.Date;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.woory.backend.entity.NotificationType;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public interface NotificationDto {
	Long getNotificationId();

	Long getGroupId();

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
	Optional<Date> getTopicDate();

	Optional<Long> getTopicId();

	Optional<String> getTopicTitle();

	Optional<Long> getContentUserId();

	Optional<Long> getContentId();

	Optional<Long> getCommentUserId();

	Optional<Long> getCommentId();

	Optional<Long> getReplyUserId();

	Optional<Long> getReplyId();

	Optional<Long> getReactionUserId();

	Optional<Long> getReactionId();

	Optional<Long> getUserId();

	NotificationType getNotificationType();

	Optional<Date> getIssueDate();

	Optional<String> getAuthor();

}
