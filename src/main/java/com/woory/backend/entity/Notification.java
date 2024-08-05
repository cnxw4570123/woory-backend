package com.woory.backend.entity;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class Notification {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)

	@Column(name = "notification_id")
	private Long id;

	private Long groupId;

	@Temporal(TemporalType.DATE)
	private Date topicDate;

	private Long topicId;

	private String topicTitle;

	private Long contentUserId;

	private Long contentId;

	private Long commentUserId;

	private Long commentId;

	private Long replyUserId;

	private Long replyId;

	private Long reactionUserId;

	private Long reactionId;

	private Long userId;

	@Enumerated(EnumType.STRING)
	private NotificationType notificationType;

	@Temporal(TemporalType.TIMESTAMP)
	private Date issueDate;

	// 토픽 생성 시
	public static Notification fromCreatingTopic(Long groupId, Long topicId, Date topicDate, String topicTitle, Date now) {
		return Notification.builder()
			.groupId(groupId)
			.topicId(topicId)
			.topicTitle(topicTitle)
			.topicDate(topicDate)
			.notificationType(NotificationType.TOPIC)
			.issueDate(now)
			.build();
	}

	// 새 글 생성 시
	public static Notification fromCreatingContent(Long groupId, Long contentUserId, Long contentId, Date now) {
		return Notification.builder()
			.groupId(groupId)
			.contentUserId(contentUserId)
			.contentId(contentId)
			.notificationType(NotificationType.CONTENT)
			.issueDate(now)
			.build();
	}

	public static Notification fromCreatingComment(Long groupId, Long contentId, Long commentUserId, Long commentId, Long userId, Date now) {
		return Notification.builder()
			.groupId(groupId)
			.contentId(contentId) // 원 글로 이동하기 위한 게시글 아이디
			.commentUserId(commentUserId) // 댓글 작성자
			.commentId(commentId)
			.userId(userId) // 원 글 작성자
			.notificationType(NotificationType.REACTION_COMMENT)
			.issueDate(new Date())
			.build();
	}

	public static Notification fromCreatingReply(Long groupId, Long contentId, Long replyUserId, Long replyId, Long userId, Date now) {
		return Notification.builder()
			.groupId(groupId)
			.contentId(contentId) // 원 글로 이동하기 위한 게시글 아이디
			.replyUserId(replyUserId) // 답글 작성자
			.replyId(replyId)
			.userId(userId) // 원 댓글 작성자
			.notificationType(NotificationType.REACTION_REPLY)
			.issueDate(now)
			.build();
	}

	public static Notification fromCreatingEmoji(Long groupId, Long contentId, Long reactionUserId, Long reactionId, Long userId, Date now) {
		return Notification.builder()
			.groupId(groupId)
			.contentId(contentId)
			.reactionUserId(reactionUserId) // 반응 작성자
			.reactionId(reactionId)
			.userId(userId) // 원 글 작성자
			.notificationType(NotificationType.REACTION_EMOJI)
			.issueDate(now)
			.build();
	}

}
