package com.woory.backend.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.woory.backend.dto.NotificationDto;
import com.woory.backend.dto.NotificationResponseDto;
import com.woory.backend.entity.Notification;
import com.woory.backend.entity.NotificationType;
import com.woory.backend.error.CustomException;
import com.woory.backend.error.ErrorCode;
import com.woory.backend.repository.GroupUserRepository;
import com.woory.backend.repository.NotificationRepository;
import com.woory.backend.utils.SecurityUtil;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NotificationService {
	private static final Logger log = LoggerFactory.getLogger(NotificationService.class);
	private final NotificationRepository notificationRepository;
	private final GroupUserRepository groupUserRepository;

	@Transactional
	public void storeNotification(Notification notification) {
		notificationRepository.save(notification);
	}

	public List<NotificationResponseDto> getNotifications(Long groupId) {
		Long userId = SecurityUtil.getCurrentUserId();

		groupUserRepository.findByUser_UserIdAndGroup_GroupId(userId, groupId)
			.orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND_IN_GROUP));

		List<NotificationDto> allByUserId = notificationRepository.findAllByUserId(groupId, userId);
		allByUserId.removeIf(n -> n.getContentUserId() != null && n.getContentUserId().equals(userId));

		return allByUserId.stream()
			.map(this::byNotificationType)
			.toList();
	}

	private NotificationResponseDto byNotificationType(NotificationDto n) {
		NotificationType notificationType = n.getNotificationType();
		if (notificationType.equals(NotificationType.TOPIC)) {
			return NotificationResponseDto.fromTopicNotification(n);
		}

		if (notificationType.equals(NotificationType.CONTENT)) {
			return NotificationResponseDto.fromContentNotification(n);
		}

		// 반응 알림인 경우
		return NotificationResponseDto.fromReactionNotification(n);
	}
}
