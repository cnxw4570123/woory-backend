package com.woory.backend.service;

import org.springframework.stereotype.Service;

import com.woory.backend.entity.Notification;
import com.woory.backend.repository.NotificationRepository;

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
