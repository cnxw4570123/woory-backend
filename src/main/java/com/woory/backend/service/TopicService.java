package com.woory.backend.service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

import com.woory.backend.entity.Notification;
import com.woory.backend.entity.Topic;
import com.woory.backend.error.CustomException;
import com.woory.backend.error.ErrorCode;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.woory.backend.dto.TopicDto;
import com.woory.backend.entity.TopicManager;
import com.woory.backend.entity.TopicSet;
import com.woory.backend.repository.GroupRepository;
import com.woory.backend.repository.NotificationRepository;
import com.woory.backend.repository.TopicRepository;
import com.woory.backend.repository.TopicSetRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class TopicService {

	private static final Logger log = LoggerFactory.getLogger(TopicService.class);
	private final TopicRepository topicRepository;
	private final GroupRepository groupRepository;
	private final TopicSetRepository topicSetRepository;
	private final NotificationRepository notificationRepository;

	@Scheduled(cron = "0 0 0 * * *", zone = "Asia/Seoul") // 매일 0시에 실행
	public void generateTopics() {
		log.info("토픽 생성 시작");
		// 새 토픽 생성
		long topic = TopicManager.pollTopicOfToday();

		TopicSet topicSet = topicSetRepository.findTopicSetById(topic)
			.orElseThrow(() -> new CustomException(ErrorCode.TOPIC_NOT_FOUND));
		Date now = new Date();

		List<TopicDto> topicDtos = groupRepository.findAll().stream()
			.map(group ->
				TopicDto.fromTopicSetWithGroupIdAndDate(topicSet, now, group.getGroupId())
			).toList();

		topicRepository.saveAll(topicDtos);
	}

	/**
	 * 매일 오전 10시에 토픽알림 생성
	 */
	@Scheduled(cron = "0 0 10 * * *", zone = "Asia/Seoul")
	public void saveTopicNotifications() {
		log.info("토픽 알람 생성 시작");

		Date today = new Date();
		List<Notification> notifications = topicRepository.findAllWithGroupsByIssueDate(today)
			.stream()
			.map(t -> Notification.fromCreatingTopic(t.getGroup().getGroupId(), t.getTopicId(), t.getIssueDate(),
				t.getTopicContent(), today))
			.toList();

		notificationRepository.saveAll(notifications);
	}
}
