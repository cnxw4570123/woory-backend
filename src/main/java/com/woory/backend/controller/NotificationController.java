package com.woory.backend.controller;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.woory.backend.service.NotificationService;
import com.woory.backend.utils.StatusUtil;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("v1/notifications")
public class NotificationController {

	private final NotificationService notificationService;

	@GetMapping("/{groupId}")
	public Map<String, Object> getNotifications(@PathVariable("groupId") Long groupId) {
		Map<String, Object> response = StatusUtil.getStatusMessage("조회 성공");
		response.put("data", notificationService.getNotifications(groupId));
		return response;
	}
}
