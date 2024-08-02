package com.woory.backend.entity;

import java.util.Date;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

@Entity
public class Notification {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
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

	@Temporal(TemporalType.DATE)
	private Date issueDate;
}
