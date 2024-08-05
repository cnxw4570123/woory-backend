package com.woory.backend.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.woory.backend.dto.NotificationDto;
import com.woory.backend.entity.Notification;
import com.woory.backend.repository.NotificationRepository;
import com.woory.backend.utils.SecurityUtil;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NotificationService {
	private final NotificationRepository notificationRepository;

	@Transactional
	public void storeNotification(Notification notification) {
		notificationRepository.save(notification);
	}
}
