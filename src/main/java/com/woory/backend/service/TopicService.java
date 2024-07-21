package com.woory.backend.service;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.woory.backend.dto.TopicDto;
import com.woory.backend.entity.TopicManager;
import com.woory.backend.entity.TopicSet;
import com.woory.backend.repository.GroupRepository;
import com.woory.backend.repository.TopicRepository;
import com.woory.backend.repository.TopicSetRepository;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class TopicService {

	private final TopicRepository topicRepository;
	private final GroupRepository groupRepository;
	private final TopicSetRepository topicSetRepository;

	@Autowired
	public TopicService(TopicRepository topicRepository, GroupRepository groupRepository,
		TopicSetRepository topicSetRepository) {
		this.topicRepository = topicRepository;
		this.groupRepository = groupRepository;
		this.topicSetRepository = topicSetRepository;
	}

	@Scheduled(cron = "0 0 0 * * *") // 매일 0시에 실행
	public void generateTopics() {
		// 새 토픽 생성
		long topic = TopicManager.pollTopicOfToday();

		TopicSet topicSet = topicSetRepository.findTopicSetById(topic).orElseThrow(() -> new RuntimeException(""));
		Date now = new Date();

		List<TopicDto> topicDtos = groupRepository.findAll().stream()
			.map(group ->
				TopicDto.fromTopicSetWithGroupIdAndDate(topicSet, now, group.getGroupId())
			).toList();

		topicRepository.saveAll(topicDtos);
	}
}
